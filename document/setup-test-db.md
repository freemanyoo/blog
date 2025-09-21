# 테스트 환경을 위한 H2 데이터베이스 설정

이 문서는 Spring Boot 프로젝트에서 테스트를 실행할 때 발생하는 데이터베이스 연결 문제를 해결하고, 효율적인 테스트 환경을 구축하는 방법을 설명합니다.

---

## 1. 문제 상황: 테스트 시 데이터베이스 연결 오류

애플리케이션 실행(`bootRun`)은 `dotenv-java` 라이브러리를 통해 `.env` 파일의 MariaDB 정보를 성공적으로 로드하여 데이터베이스에 연결할 수 있었습니다. 하지만 `./gradlew build` 명령을 실행하여 테스트를 수행할 때 `BeanCreationException`과 함께 데이터베이스 연결 오류가 발생했습니다.

- **원인:** Spring Boot의 테스트 환경은 메인 애플리케이션의 `main` 메소드를 실행하지 않습니다. 따라서 `main` 메소드에 작성된 `Dotenv` 로딩 코드가 테스트 시에는 동작하지 않아, `.env` 파일의 MariaDB 접속 정보가 테스트 환경에 전달되지 않았습니다. 이로 인해 테스트는 유효하지 않은(또는 없는) 데이터베이스 정보로 연결을 시도하다가 실패했습니다.

---

## 2. 해결 방법 및 이유: 인메모리 H2 데이터베이스 사용

테스트 환경에서 데이터베이스 연결 문제를 해결하는 가장 표준적이고 권장되는 방법은 **인메모리(In-memory) 데이터베이스인 H2**를 사용하는 것입니다.

- **해결 방법:** `src/test/resources/application.properties` 파일을 생성하고, 이 파일에 H2 데이터베이스 설정을 추가합니다. Spring Boot는 테스트 실행 시 `src/test/resources` 경로의 설정 파일을 우선적으로 로드합니다.

- **H2를 사용하는 이유 (장점):**
    -   **독립성 (Isolation):** 테스트가 외부 데이터베이스의 상태에 의존하지 않고 독립적으로 실행됩니다. 실제 DB에 데이터가 없거나, DB 서버가 꺼져 있어도 테스트는 항상 동일한 환경에서 실행됩니다.
    -   **속도 (Speed):** H2는 디스크가 아닌 메모리에서 동작하므로, 실제 데이터베이스에 접근하는 것보다 훨씬 빠르게 테스트를 수행할 수 있습니다.
    -   **신뢰성 (Reliability):** 네트워크 문제, DB 서버 다운, 잘못된 자격 증명 등 외부 요인으로 인한 테스트 실패를 방지하여 테스트의 신뢰성을 높입니다.
    -   **단순성 (Simplicity):** 별도의 테스트용 데이터베이스 서버를 설치하거나 관리할 필요가 없습니다.

---

## 3. 설정 단계 (Step-by-Step)

### 1단계: `build.gradle`에 H2 의존성 추가

`dependencies {}` 블록에 H2 데이터베이스 의존성을 `testRuntimeOnly` 스코프로 추가합니다. 이는 H2가 테스트 실행 시에만 포함되도록 하여, 실제 애플리케이션 빌드에는 영향을 주지 않습니다.

```groovy
// build.gradle

dependencies {
    // ... 다른 의존성들
    testRuntimeOnly 'com.h2database:h2'
    // ...
}
```

### 2단계: `src/test/resources/application.properties` 생성 및 설정

`src/test/resources` 디렉토리에 `application.properties` 파일을 생성하고, 다음 H2 데이터베이스 설정을 추가합니다.

```properties
# src/test/resources/application.properties

# Use H2 in-memory database for tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Use H2 dialect for Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

이 설정을 통해, `./gradlew build` 명령으로 테스트를 실행할 때 Spring Boot는 자동으로 H2 인메모리 데이터베이스를 사용하여 테스트를 수행하게 됩니다.
