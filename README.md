# 카카오페이 사전과제
## 기술 스택
* Java 1.8
* Spring Boot v1.5.22
* Gradle v4.8.1
* JPA/Hibernate, local h2DB

## 서버 실행
* VM Argument 에 -Dspring.profiles.active=local 옵션을 주고 실행한다.

## 구조
### Controller
뿌리기 API, 받기 API, 조회 API 제공

### Service
뿌리기, 받기, 조회를 위한 로직 제공

### Entity
SprinklingStatementEntity 와 DistributedInfoEntity 는 1:N 관계, 엔티티 2개로 구성
* SprinklingStatementEntity: 뿌리기 내역 관리 엔티티
* DistributedInfoEntity: 뿌린 돈 분배 내역 엔티티  
여러 유저가 동시에 받아가는 것을 방지하기 위해 version 필드 생성
(낙관적 락, 두 번의 갱신 분실 문제(second lost updates problem) 해결을 위한 필드)

### ExceptionHandler
서비스 계층에서 로직 수행시 발생한 예외를 RestResponse 로 변환해 리턴

## 핵심 문제 해결 전략
### 1. 뿌리기 API
* 문자열 3자리로 token 을 생성한다.
* SprinklingStatement 를 저장한다.
* 저장이 제대로 되었다면 statementId 가 발급되고, RandomGenerator 를 이용해 돈을 랜덤으로 분배한 후, 
해당 정보로 DistributedInfoEntity 리스트를 저장한다.

### 2. 받기 API
* 파라미터 검증 및 예외상황을 처리한다.
    * 데이터 없음
    * 토큰 만료
    * 뿌린 유저가 받아가려는 경우
    * 이미 한 번 받은 유저가 또 받아가려는 경우
    * 해당 뿌리기가 이미 모두 받기 완료된 경우
* 위 예외상황이 아니라면 DistributedInfoEntity 에 유저 정보를 기입하고, 받은 돈을 리턴한다.  
여러 유저가 동일한 돈을 받으려는 동시성 문제는 DistributedInfoEntity version 필드를 이용해 해결하고,
이 경우 최초 커밋 유저를 제외한 유저는 ObjectOptimisticLockingFailureException 이 발생한다.

### 3. 조회 API
* 파라미터 검증 및 예외상황을 처리한다.
    * 데이터 없음
    * 뿌린 유저가 아닌 경우
    * 조회 기간 만료(7일)
* 위 예외상황이 아니라면 해당 뿌리기 내역을 리턴한다.