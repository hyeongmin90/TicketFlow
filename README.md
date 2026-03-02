# TicketFlow Backend Monorepo

티켓 예매 시스템의 핵심 비즈니스 로직과 API 서빙을 담당하는 백엔드 모노레포 프로젝트입니다.

## 🏗 프로젝트 구조

본 프로젝트는 Gradle 멀티 프로젝트 빌드 체계를 사용하며, 다음과 같은 모듈로 구성되어 있습니다.

```text
backend-monorepo/
├── ticket-service/      # 핵심 비즈니스 로직 (구 demo)
│   ├── 예매 프로세스 처리 (JPA, MySQL)
│   ├── 분산 락 구현 (Redisson)
│   └── 이벤트 발행 (Kafka)
├── api_server/          # API 게이트웨이 및 서빙 모듈
│   ├── 클라이언트 요청 처리 (WebMVC)
│   └── 실시간 상태 알림 (SSE, Reactive Redis)
└── gradle/              # 공통 빌드 설정 및 Wrapper
```

## 🛠 기술 스택

- **Language:** Java 21
- **Framework:** Spring Boot 4.0.3
- **Build Tool:** Gradle 9.3.0
- **Database:** MySQL, Redis
- **Concurrency Control:** Redisson (Distributed Lock)
- **Messaging:** Apache Kafka
- **Communication:** WebMVC, Server-Sent Events (SSE)

## 🚀 시작 가이드

### 요구 사항
- Java 21 이상
- Docker (Redis, Kafka, MySQL 실행용)

### 빌드 방법
루트 디렉토리에서 다음 명령어를 실행합니다.
```bash
./gradlew clean build
```

### 개별 모듈 실행
- **Ticket Service:**
  ```bash
  ./gradlew :ticket-service:bootRun
  ```
- **API Server:**
  ```bash
  ./gradlew :api_server:bootRun
  ```

## 🔗 주요 기능

1. **대규모 트래픽 처리:** Redis와 Kafka를 활용한 비동기 예매 처리 및 부하 분산.
2. **동시성 제어:** Redisson 분산 락을 통해 동일 좌석에 대한 중복 예매 방지.
3. **실시간 알림:** SSE(Server-Sent Events)를 통한 예매 성공 여부 실시간 전송.
4. **모노레포 관리:** 하위 모듈 간의 의존성 및 공통 설정을 루트에서 효율적으로 관리.
