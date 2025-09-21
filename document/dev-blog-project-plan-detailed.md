# DevCodeBlog: 상세 프로젝트 기획서

## 1. 프로젝트 개요

### 1.1 프로젝트 명
**DevCodeBlog** - 코드 작성, 실행 결과, 메모를 통합 관리하는 개발자 블로그

### 1.2 프로젝트 목적
- 개발자가 코드 작성 과정과 실행 결과, 학습 메모를 하나의 플랫폼에서 체계적으로 관리
- 코드 스니펫과 설명을 효과적으로 공유하고 기록할 수 있는 개인 블로그 구축
- 학습한 내용과 문제 해결 과정을 시각적으로 정리하여 포트폴리오 역할 수행

### 1.3 타겟 사용자
- 개인 개발자 (1차 타겟)
- 프로그래밍 학습자
- 기술 블로그 운영을 원하는 개발자

---

## 2. 핵심 기능 상세 요구사항

### 2.1 포스트 관리
- **사용자 스토리:**
    - 사용자는 새로운 포스트를 생성, 수정, 삭제할 수 있다.
    - 사용자는 포스트를 공개 또는 비공개로 설정할 수 있다.
    - 사용자는 자신의 모든 포스트 목록을 확인할 수 있다.

### 2.2 코드 에디터 (Monaco Editor)
- **요구사항:**
    - JavaScript, Python, Java, SQL 문법 하이라이팅 지원
    - 코드 자동 완성, 괄호 매칭, 기본 코드 포맷팅 기능
    - 다크/라이트 테마 전환 기능
    - 작성된 코드를 클립보드로 복사하는 버튼 제공

### 2.3 코드 실행
- **요구사항:**
    - **클라이언트 사이드 실행 (1차 목표):** JavaScript 코드는 웹 브라우저에서 즉시 실행하고 결과를 콘솔에 출력한다.
    - **서버 사이드 실행 (2차 목표):** Python, Java 코드는 서버의 안전한 샌드박스 환경에서 실행하고 결과를 반환한다. (보안상 초기 버전에서는 제외 가능)
    - 실행 시간 초과 (Timeout) 설정 (예: 5초)

### 2.4 마크다운 에디터
- **요구사항:**
    - 실시간 미리보기 기능 (좌측: 마크다운, 우측: 렌더링 결과)
    - 기본 마크다운 문법 지원 (헤더, 목록, 링크, 이미지, 인용, 코드 블록)
    - 이미지 업로드 및 삽입 기능

### 2.5 사용자 인증
- **요구사항:**
    - 이메일과 비밀번호를 이용한 회원가입
    - JWT 토큰 기반의 로그인/로그아웃
    - 로그인한 사용자는 자신의 프로필 정보 관리 가능

---

## 3. 정제된 데이터베이스 설계

```sql
-- 사용자 (users)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 카테고리 (categories)
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 포스트 (posts)
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(255) NOT NULL,
    content TEXT, -- 마크다운 텍스트
    code_content TEXT, -- 코드
    language VARCHAR(50),
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 태그 (tags)
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 포스트-태그 관계 (post_tags)
CREATE TABLE post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
```

---

## 4. 상세 API 명세 (RESTful API)

### 4.1 사용자 (Users)
- **`POST /api/auth/signup`**: 회원가입
    - **Request Body**: `{ "username": "testuser", "email": "test@example.com", "password": "password123" }`
    - **Response (201 Created)**: `{ "message": "User registered successfully" }`
- **`POST /api/auth/login`**: 로그인
    - **Request Body**: `{ "email": "test@example.com", "password": "password123" }`
    - **Response (200 OK)**: `{ "accessToken": "jwt.token.string" }`

### 4.2 포스트 (Posts)
- **`POST /api/posts`**: 새 포스트 작성
    - **Headers**: `Authorization: Bearer <jwt-token>`
    - **Request Body**: `{ "title": "...", "content": "...", "code_content": "...", "language": "javascript", "category_id": 1, "tags": ["js", "react"], "is_public": true }`
    - **Response (201 Created)**: `{ "id": 1, "title": "...", ... }`
- **`GET /api/posts`**: 모든 공개 포스트 목록 조회 (페이지네이션)
    - **Query Params**: `?page=0&size=10&sort=createdAt,desc`
    - **Response (200 OK)**: `[{ "id": 1, "title": "...", "author": "...", ... }]`
- **`GET /api/posts/{id}`**: 특정 포스트 상세 조회
    - **Response (200 OK)**: `{ "id": 1, "title": "...", "content": "...", ... }`
- **`PUT /api/posts/{id}`**: 포스트 수정
    - **Headers**: `Authorization: Bearer <jwt-token>`
    - **Request Body**: `{ "title": "updated title", ... }`
    - **Response (200 OK)**: `{ "id": 1, "title": "updated title", ... }`
- **`DELETE /api/posts/{id}`**: 포스트 삭제
    - **Headers**: `Authorization: Bearer <jwt-token>`
    - **Response (204 No Content)**

### 4.3 카테고리 (Categories) & 태그 (Tags)
- **`GET /api/categories`**: 모든 카테고리 목록 조회
- **`GET /api/tags`**: 모든 태그 목록 조회 (태그 클라우드용)

---

## 5. 구체적인 UI/UX 레이아웃

### 5.1 메인 페이지 (`/`)
- **Header**: 로고, 검색창, `글 작성` 버튼, `로그인/회원가입` 또는 `사용자 프로필`
- **Sidebar**: 카테고리 목록 (클릭 시 필터링), 태그 클라우드
- **Main Content**: 포스트 목록 (카드 형태, 제목/작성자/요약/태그 표시)
- **Footer**: 저작권 정보

### 5.2 포스트 작성/수정 페이지 (`/write` or `/edit/{id}`)
- **Layout**: 2단 분할 (좌: 코드 에디터, 우: 설명 작성)
- **Header**: `저장`, `공개/비공개` 토글 버튼
- **Left Pane (Code)**: Monaco Editor, 상단에 언어 선택 드롭다운, `코드 실행` 버튼, 실행 결과 표시 영역
- **Right Pane (Text)**: 제목 입력 필드, 마크다운 에디터, 카테고리 선택, 태그 입력

### 5.3 포스트 상세 페이지 (`/posts/{id}`)
- **Header**: 포스트 제목, 작성자, 작성일, 카테고리
- **Body**: 렌더링된 마크다운 콘텐츠와 코드 블록이 순서대로 표시
- **Code Block**: 우측 상단에 `복사` 버튼
- **Footer**: 이전/다음 포스트 링크, 댓글 영역

---

## 6. 세분화된 개발 일정 (1단계)

### Week 1: 백엔드 기본 설정
- **Day 1-2**: Spring Boot 프로젝트 설정, `build.gradle`에 `Spring Web`, `Spring Data JPA`, `MariaDB Driver`, `Spring Security`, `JWT` 의존성 추가
- **Day 3**: `application.properties`에 DB 연결 정보 및 JWT secret key 설정
- **Day 4-5**: DB 스키마 기반으로 `User`, `Category`, `Post`, `Tag` JPA 엔티티 클래스 및 관계 매핑 구현

### Week 2: 핵심 API 구현
- **Day 1-2**: `UserRepository` 생성 및 Spring Security, JWT를 이용한 사용자 회원가입/로그인 API 구현
- **Day 3-4**: `PostRepository`, `CategoryRepository`, `TagRepository` 생성. 포스트 생성/수정/삭제 API 구현 (작성자 인증 포함)
- **Day 5**: 포스트 목록 및 상세 조회 API 구현. 페이지네이션 적용.

### Week 3: 프론트엔드 기본 설정
- **Day 1-2**: React (Vite) 프로젝트 생성, `react-router-dom` 설치 및 기본 라우팅 설정 (`/`, `/write`, `/posts/{id}`)
- **Day 3**: Tailwind CSS 설정 및 기본 레이아웃 컴포넌트 (Header, Footer, Sidebar) 생성
- **Day 4-5**: 메인 페이지 UI 구현. API와 연동하여 포스트 목록 표시.

### Week 4: 핵심 페이지 구현
- **Day 1-2**: 포스트 상세 페이지 UI 구현. API와 연동하여 포스트 내용 표시. `react-markdown`으로 콘텐츠 렌더링.
- **Day 3-4**: 포스트 작성/수정 페이지 UI 구현. `Monaco Editor`와 마크다운 에디터 연동.
- **Day 5**: 작성/수정 페이지에서 `저장` 버튼 클릭 시 API 호출하여 포스트 생성/수정 기능 완료.

