# TicketFlow

TicketFlow는 티켓 예약 시스템의 동시성 제어 및 대기열 처리를 학습하기 위한 데모 프로젝트입니다.

> **⚠️ 현재 진행 중인 프로젝트입니다.**
>
> 이 프로젝트는 **동시성(Concurrency) 제어**와 **데이터 정합성(Data Consistency)** 문제를 학습하고 해결해 보는 과정을 기록하고 있습니다. 
> Redis Streams와 Redisson 분산 락 등을 활용하여 트래픽 처리와 데이터 정합성 유지 방법을 고민하고 구현해 보았습니다.

Redis Streams를 활용한 대기열 처리와 Redisson 분산 락을 적용해 보았습니다.

## 🛠 기술 스택 (Tech Stack)

- **Language**: Java 21
- **Framework**: Spring Boot
- **Database**:
  - MySQL (RDBMS)
  - Redis (Cache & Streams)
- **ORM**: Spring Data JPA
- **Concurrency Control**: Redisson (Distributed Lock)

## 🚀 주요 기능 (Key Features)

### 1. 좌석 예약 (Seat Reservation)
   - 사용자가 공연 좌석을 예약하는 기능을 구현했습니다.
   - 예약 요청을 Redis Streams로 비동기 처리하여 부하 분산을 시도했습니다.

### 2. 동시성 제어 (Concurrency Control)
   - **Redisson 분산 락**을 활용하여 동시 예약 요청 시 발생할 수 있는 데이터 정합성 문제를 해결하고자 했습니다.
   - `RedissonLockTicketFacade`를 통해 락 획득 및 해제 로직을 구현했습니다.

### 3. 이벤트 기반 아키텍처 (Event-Driven Architecture)
   - `ReserveStreamListener`가 Redis Stream(`reserve:1`)의 메시지를 소비(Consume)하여 예약 로직을 수행하도록 구성했습니다.
   - 예약 요청 접수와 실제 처리 로직을 분리하여 시스템 구조를 개선해 보았습니다.

## 📂 프로젝트 구조 (Project Structure)

```
src/main/java/com/example/demo
├── config      # 설정 파일 (RedisConfig 등)
├── domain      # 도메인 엔티티 및 DTO (User, Seat, Reservation 등)
├── infra       # 인프라스트럭처 (Controller, Stream Listener)
└── service     # 비즈니스 로직 (ReserveService, Facade 패턴)
```

## 🏁 시작하기 (Getting Started)

### 사전 요구사항 (Prerequisites)
로컬 환경에 다음 서비스가 실행 중이어야 합니다.
- **MySQL**: 3306 포트 (`reserve_db` 데이터베이스)
- **Redis**: 6379 포트

### 설치 및 실행 (Installation & Run)

1. **프로젝트 클론**
   ```bash
   git clone <repository-url>
   cd ticketflow
   ```

2. **환경 설정**
   `src/main/resources/application.yaml` 파일에서 데이터베이스 및 Redis 연결 정보를 확인하고 필요시 수정합니다.

3. **애플리케이션 실행**
   Gradle Wrapper를 사용하여 애플리케이션을 실행합니다.
   ```bash
   ./gradlew bootRun
   ```

## 📝 라이선스 (License)
이 프로젝트는 MIT 라이선스를 따릅니다.
