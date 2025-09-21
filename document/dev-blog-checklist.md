# DevCodeBlog 개발 체크리스트

## 1단계: 기본 구조 및 핵심 기능

### Week 1: 백엔드 기본 설정
- [ ] **Spring Boot 프로젝트 의존성 설정 (`build.gradle`)**
    - [ ] `Spring Web` 추가
    - [ ] `Spring Data JPA` 추가
    - [ ] `MariaDB Driver` 추가
    - [ ] `Spring Security` 추가
    - [ ] `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (JWT용) 추가
- [ ] **`application.properties` 설정**
    - [ ] 데이터베이스 연결 정보 (URL, username, password) 설정
    - [ ] JPA/Hibernate 설정 (e.g., `spring.jpa.hibernate.ddl-auto=update`)
    - [ ] JWT secret key 및 만료 시간 설정
- [ ] **JPA 엔티티 및 관계 매핑**
    - [ ] `User` 엔티티 클래스 생성
    - [ ] `Category` 엔티티 클래스 생성
    - [ ] `Post` 엔티티 클래스 생성
    - [ ] `Tag` 엔티티 클래스 생성
    - [ ] `PostTag` 중간 테이블을 포함한 관계 설정 (`@OneToMany`, `@ManyToMany`)

### Week 2: 핵심 API 구현
- [ ] **사용자 인증 API**
    - [ ] `UserRepository` 인터페이스 생성
    - [ ] `UserDetailsService` 구현
    - [ ] `PasswordEncoder` 빈 등록
    - [ ] 회원가입 API (`POST /api/auth/signup`) 컨트롤러 및 서비스 로직 구현
    - [ ] 로그인 API (`POST /api/auth/login`) 컨트롤러 및 서비스 로직 구현 (JWT 토큰 생성 포함)
    - [ ] JWT 인증 필터 설정 (`JwtAuthenticationFilter`)
- [ ] **포스트/카테고리/태그 API**
    - [ ] `PostRepository`, `CategoryRepository`, `TagRepository` 인터페이스 생성
    - [ ] 포스트 생성 API (`POST /api/posts`) 구현
    - [ ] 포스트 수정 API (`PUT /api/posts/{id}`) 구현
    - [ ] 포스트 삭제 API (`DELETE /api/posts/{id}`) 구현
    - [ ] 포스트 목록 조회 API (`GET /api/posts`) 구현 (페이지네이션 포함)
    - [ ] 포스트 상세 조회 API (`GET /api/posts/{id}`) 구현
    - [ ] 카테고리 목록 조회 API (`GET /api/categories`) 구현
    - [ ] 태그 목록 조회 API (`GET /api/tags`) 구현

### Week 3: 프론트엔드 기본 설정
- [ ] **React 프로젝트 설정**
    - [ ] Vite를 이용해 React 프로젝트 생성 (`npm create vite@latest`)
    - [ ] `react-router-dom` 설치 및 라우터 설정
    - [ ] `axios` 설치 및 API 클라이언트 인스턴스 설정
- [ ] **UI 레이아웃 및 페이지**
    - [ ] Tailwind CSS 설정
    - [ ] `Header`, `Footer`, `Sidebar` 레이아웃 컴포넌트 생성
    - [ ] 메인 페이지 (`/`) 라우트 및 컴포넌트 생성
    - [ ] 글 작성 페이지 (`/write`) 라우트 및 컴포넌트 생성
    - [ ] 글 상세 페이지 (`/posts/:id`) 라우트 및 컴포넌트 생성
- [ ] **API 연동**
    - [ ] 메인 페이지에서 `GET /api/posts` API 호출하여 포스트 목록 표시
    - [ ] Redux Toolkit 또는 Context API 설정 (글로벌 상태관리용)

### Week 4: 핵심 페이지 기능 구현
- [ ] **포스트 상세 페이지**
    - [ ] `useParams`로 post id를 받아 `GET /api/posts/{id}` API 호출
    - [ ] `react-markdown` 라이브러리를 사용하여 마크다운 콘텐츠 렌더링
    - [ ] 코드 블록에 대한 하이라이팅 적용 (`react-syntax-highlighter`)
- [ ] **포스트 작성/수정 페이지**
    - [ ] `Monaco Editor` 컴포넌트 연동 (`@monaco-editor/react`)
    - [ ] 마크다운 에디터 컴포넌트 연동 (e.g., `@uiw/react-md-editor`)
    - [ ] 제목, 카테고리, 태그 입력 UI 구현
    - [ ] '저장' 버튼 클릭 시 `POST /api/posts` 또는 `PUT /api/posts/{id}` API 호출 로직 구현
