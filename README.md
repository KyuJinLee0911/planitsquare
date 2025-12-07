# planitsquare
플랜잇스퀘어 백엔드 개발자 과제 전형입니다.
| 속성 | 버전/도구 |
| --- | --- |
| Java | 21 |
| Gradle | 8.14.3 |
| Spring Boot | 3.4.12 |
| DB | H2(Inmemory) |
| ORM | JPA(Hibernate) |
| Query | QueryDSL 5.0.0 |
| Test | JUnit5, Mockito |

> 별도의 DB 서버나 Docker 컨테이너를 띄울 필요 없이 애플리케이션 실행만으로 바로 테스트할 수 있습니다.

# 빌드 및 실행방법
## 빌드
### 1) 의존성 설치 및 빌드
프로젝트 루트에서 다음 명령어를 실행합니다.

**Mac / Linux**
```bash
./gradlew clean build
```
**Windows**
```bash
gradlew clean build
```
빌드가 완료되면 `build/libs` 디렉토리에 `subject-0.0.1-SNAPSHOPT.jar` 파일이 생성됩니다.

## 실행 방법
### 1) Gradle로 바로 실행
**Mac / Linux**
```bash
./gradlew bootRun
```
**Windows**
```bash
gradlew bootRun
```
기본 프로파일로 실행되며, H2 인메모리 DB를 사용하도록 설정되어 있습니다.
설정 내용은 `src/main/resources/application.yml` 에 있습니다.

### 2) Jar 파일로 실행
먼저 빌드를 실행한 후 생성된 Jar를 실행합니다.

```bash
java -jar build/libs/subject-0.0.1-SNAPSHOPT.jar
```

# 설계한 REST API 명세
최초 실행 시 데이터가 없다면 외부 API를 통해 데이터를 받아옵니다.
## 1. 데이터 검색
* 엔드포인트 : HTTP GET (/api/holidays)
* 파라미터
  * **RequestParameter**
    * Integer year `ex) 2025`
    * String countryCode `ex) KR`
    * LocalDate from `ex) 2024-01-01`
    * LocalDate to `ex) 2025-06-30` 
    * String type `ex) Public`
    * Pageable pageable
      ```
      {
        "page": 0,
        "size": 30,
        "sort" : [
        ]
      }
      ```
* **응답**

  조건에 맞는 데이터를 페이징 형태로 반환합니다.
  <details>
  <summary>응답 예시</summary>
    
      {
        "code": "SUCCESS",
        "message": null,
        "data": {
          "content": [
            {
              "holidayId": 5511,
              "date": "2025-01-01",
              "name": "New Year's Day",
              "localName": "새해",
              "countryCode": "KR",
              "launchYear": null,
              "counties": [],
              "types": [
                "Public"
              ]
            },
          "pageable": {
            "pageNumber": 0,
            "pageSize": 30,
            "sort": {
              "empty": false,
              "sorted": true,
              "unsorted": false
            },
            "offset": 0,
            "paged": true,
            "unpaged": false
          },
          "last": true,
          "totalElements": 15,
          "totalPages": 1,
          "size": 30,
          "number": 0,
          "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
          },
          "first": true,
          "numberOfElements": 15,
          "empty": false
        }
      }
  </details>

## 2. 재동기화
* **엔드포인트** : HTTP POST (/api/holidays)
* **파라미터**
  * **RequestBody**
    ```json
    {
      "year": 2025,
      "countryCode": "KR"
    }
    ```
* **응답**
  
  응답 데이터로 새로 저장된 데이터의 개수를 반환합니다.
  <details>
  <summary>응답 예시</summary>
    
      {
        "code": "SUCCESS",
        "message": null,
        "data": 15
      }
   </details>

## 3. 데이터 삭제
* 엔드포인트 : HTTP DELETE (/api/holidays)
* 파라미터
  * **RequestBody**
    ```json
    {
      "year": 2025,
      "countryCode": "KR"
    }
    ```
* **응답**
    
  따로 반환하는 응답 데이터는 없습니다.
  <details>
  <summary>응답 예시</summary>
    
      {
        "code": "SUCCESS",
        "message": null,
        "data": null
      }
   </details>
# ./gradlew clean test 성공 스크린샷
<img width="1876" height="652" alt="image" src="https://github.com/user-attachments/assets/5ff418af-a71e-4ac2-bde7-2ff0a5002316" />

# API 문서 (Swagger / Open API)
애플리케이션 실행 후 아래 URL에서 Swagger UI를 통해 API 명세를 확인할 수 있습니다.
### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```
### OpenAPI JSON 문서
```
http://localhost:8080/v3/api-docs
```
