# `dotenv-java`를 이용한 환경 변수 설정 가이드

이 문서는 Spring Boot 프로젝트에서 `.env` 파일을 사용하여 데이터베이스 접속 정보나 JWT 비밀 키 같은 민감한 정보를 안전하게 관리하는 방법을 설명합니다.

---

## 1. 문제 상황: `dotenv-gradle` 플러그인 빌드 실패

최초에는 `.env` 파일을 빌드 시점에 적용하기 위해 `dotenv-gradle`이라는 Gradle 플러그인을 사용하려고 시도했습니다.

- **초기 접근 방식:** `build.gradle`의 `plugins {}` 블록에 `io.github.cdimascio.dotenv-gradle` 추가
- **발생한 문제:** Gradle이 해당 플러그인을 저장소에서 찾지 못하는 `Plugin [...] was not found` 오류가 발생했습니다.
- **시도한 해결책:**
    1. `settings.gradle`에 `gradlePluginPortal()` 저장소 추가
    2. 플러그인 ID 오타 수정 (`dotenv` -> `dotenv-gradle`)
    3. Gradle 캐시 삭제 (`--refresh-dependencies`)
- **결론:** 위 해결책들을 모두 시도했음에도 불구하고, 사용자 PC의 네트워크 환경 또는 알 수 없는 다른 원인으로 인해 빌드 오류가 지속적으로 발생했습니다.

---

## 2. 해결 방법: `dotenv-java` 라이브러리 사용

빌드 시점의 플러그인 문제에 대한 대안으로, 애플리케이션 **런타임 시점**에 `.env` 파일을 읽는 `dotenv-java` 라이브러리를 사용하기로 결정했습니다. 이 방법은 빌드 과정에 영향을 주지 않으므로 기존의 문제를 우회할 수 있는 훌륭한 해결책입니다.

- **핵심 아이디어:** 애플리케이션이 시작될 때(`main` 메소드에서) `dotenv-java` 라이브러리가 `.env` 파일을 읽고, 그 값을 Spring Boot가 인식할 수 있도록 **시스템 속성(System Properties)**으로 직접 설정해줍니다.

---

## 3. 설정 단계 (Step-by-Step)

### 1단계: `build.gradle`에 의존성 추가

`dependencies {}` 블록에 `dotenv-java` 라이브러리를 추가합니다.

```groovy
// build.gradle

dependencies {
    // ... 다른 의존성들
    implementation 'io.github.cdimascio:dotenv-java:3.2.0'
    // ...
}
```

### 2단계: `.env` 파일 생성 및 `.gitignore` 등록

프로젝트 최상위 경로에 `.env` 파일을 생성하고, 민감한 정보를 `KEY=VALUE` 형식으로 작성합니다.

```
# .env

SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET=a-very-long-and-secure-secret-key-for-hs256-algorithm
```

**매우 중요:** 이 파일이 Git 저장소에 올라가지 않도록 `.gitignore` 파일에 `.env`를 반드시 추가해야 합니다.

```
# .gitignore

.env
```

### 3단계: `application.properties` 수정

`src/main/resources/application.properties` 파일에서, `.env` 파일의 변수를 참조하도록 `${...}` 구문을 사용합니다.

```properties
# application.properties

# Database Settings
spring.datasource.url=jdbc:mariadb://localhost:3306/blog
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# JWT Settings
jwt.secret=${JWT_SECRET}
```

### 4단계: 메인 애플리케이션 코드 수정

`BlogApplication.java`의 `main` 메소드에서 Spring Boot 애플리케이션이 실행되기 **전에**, `Dotenv`를 로드하고 시스템 속성을 설정하는 코드를 추가합니다.

```java
// BlogApplication.java

package com.freemanyoo.blog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        // .env 파일을 로드합니다.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // .env의 변수들을 시스템 속성으로 설정합니다.
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // Spring Boot 애플리케이션을 실행합니다.
        SpringApplication.run(BlogApplication.class, args);
    }

}
```

이 4단계를 통해, 빌드 오류 없이 안전하게 `.env` 파일로 민감한 정보를 관리할 수 있게 됩니다.
