# CQRS 패턴을 적용한 통신사 요금제 관리 시스템

이 프로젝트는 CQRS(Command Query Responsibility Segregation) 패턴을 적용한 통신사 요금제 관리 시스템의 데모 애플리케이션입니다.

## 아키텍처 개요

이 시스템은 다음과 같은 컴포넌트로 구성됩니다:

1. **Command Service**: 요금제 변경 및 사용량 업데이트를 처리합니다. PostgreSQL을 데이터 저장소로 사용합니다.
2. **Query Service**: 요금제 및 사용량 조회를 처리합니다. MongoDB를 데이터 저장소로 사용합니다.
3. **Azure Event Hub**: Command와 Query 서비스 간의 이벤트 메시징 플랫폼으로 사용됩니다.
4. **Azure Blob Storage**: Event Hub 체크포인팅을 위한 저장소로 사용됩니다.

## 이벤트 흐름

1. Command 서비스에서 요금제 변경이나 사용량 업데이트 요청을 받습니다.
2. Command 서비스는 PostgreSQL에 데이터를 저장하고 Event Hub로 이벤트를 발행합니다.
3. Query 서비스는 Event Hub에서 이벤트를 구독하여 MongoDB에 조회 모델을 갱신합니다.
4. 클라이언트는 최신 데이터를 Query 서비스를 통해 조회합니다.

## 설치 및 실행 방법

### 필수 조건
- Java 17 이상
- Docker 및 Docker Compose
- Gradle
- Azure Event Hub 네임스페이스 및 접근 키

### 환경 변수 설정

```shell
# Event Hub 및 Storage 연결 문자열 설정
export EVENT_HUB_CONNECTION_STRING="Endpoint=sb://<your-namespace>.servicebus.windows.net/;SharedAccessKeyName=<key-name>;SharedAccessKey=<key>"
export STORAGE_CONNECTION_STRING="DefaultEndpointsProtocol=https;AccountName=<your-account>;AccountKey=<your-key>;EndpointSuffix=core.windows.net"
```


