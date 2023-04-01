# BookLibrarySite-Demo(Backend-Repository)

## 목차

[<-프론트엔드 레포지토리 보러가기](https://github.com/Jimoou/BookLibrarySite-Frontend)

1. [설명](#설명)
2. [Overview](#overview)
3. [설계](#설계)  
   3-1. [Database](#db-mysql)  
   3-2. [API](#rest-api)

4. [기술 및 방법론](#기술-및-방법론)  
   4-1. [Test Code](#4-1-testcode-withtdd)  
   4-2. [Okta](#okta)  
   4-3. [TossPayments](#tosspayments)  
   4-4. [의존성](#4-4-의존성)  
   4-5. [스택](#4-5-스택)

## 1. 설명

이 프로젝트는 도서 구매 및 코인을 이용한 대여를 할 수 있는 웹사이트입니다. 백엔드는 Spring Boot 프레임워크를, 프론트엔드는 React를 사용하여 개발하였습니다.

[이 프로젝트의 특이점은 데이터 베이스 설계의 핵심 요소인 외래 키(Foreign Key) 설정이 없다는 점](#설계)입니다. 이러한 경험을 통해 데이터의 무결성, 데이터 모델링에 대한 이해를 높이고 REST API 설계를 통한 백엔드와 프론트엔드 간의 통신 경험을 쌓으려 했습니다.

보안과 인증을 위해 JWT를 기반으로 한 레거시 시스템 경험을 바탕으로, SSO(Single Sign-On) 기능을 외부에 맡길 수 있는 서비스를 활용하고자 했습니다. 따라서 [Okta를 사용](#okta)했습니다. 또한, 결제 시스템으로는 외부 API인 [Toss Payments를 사용](#tosspayments)하였습니다.

## 2. Overview

**https://springboot-library-add4e.web.app/home
-> 현재는 프론트 웹사이트만 배포되어 있습니다.**

<details>
<summary>접기/펼치기</summary>

### 회원가입/로그인

![springboot-library-로그인회원가](https://user-images.githubusercontent.com/109801772/229055019-52a717c7-7220-4b76-b47d-e04e89ab0f58.gif)

<img width="1423" alt="스크린샷 2023-04-01 오후 4 24 12" src="https://user-images.githubusercontent.com/109801772/229272060-6cec8b07-ce94-4f61-8868-4eb6812b65a0.png">

### 책 검색

![springboot-library-책검색조](https://user-images.githubusercontent.com/109801772/229054937-28e38616-5b47-4d50-ae68-684041fad088.gif)

### 코인 충전/결제

![springboot-library-코인충전결 (1)](https://user-images.githubusercontent.com/109801772/229059384-3e634543-9282-4ae7-bfda-9b28c06de5f5.gif)

### 리뷰 남기기

![springboot-library-책리뷰남기](https://user-images.githubusercontent.com/109801772/229057310-9fb5e7e9-ff4f-419f-aaa1-9dabfeb19ce2.gif)

### 책 대여/반납

![springboot-library-대여반](https://user-images.githubusercontent.com/109801772/229055634-1a170fa9-1f67-49ea-a211-4277d3eae9f0.gif)

### 코인 충전/사용 내역 조회

![springboot-library-코인조](https://user-images.githubusercontent.com/109801772/229055122-3fec4449-6683-452c-b01d-27a2e58f71de.gif)

### 책 장바구니 추가/구매/내역

![springboot-library-책장바구니구](https://user-images.githubusercontent.com/109801772/229055309-55d0f5fc-dcad-4596-ab93-621b93f391df.gif)

### 문의 남기기

![springboot-library-문의남기](https://user-images.githubusercontent.com/109801772/229055093-c6fea8af-b9f1-4e01-bfd0-2e1ab2d715a6.gif)

### 관리자 - 책 추가/삭제

![springboot-library-책추가-삭](https://user-images.githubusercontent.com/109801772/229055409-369f468f-80f5-412d-8364-8a88ae7c6f00.gif)

</details>

## 3. 설계

이전 프로젝트에서는 외래 키(FK)를 사용하여 데이터베이스를 설계하고 관리하는 경험을 했습니다. 하지만 이번 프로젝트에서는 의도적으로 외래 키를 사용하지 않고 데이터베이스를 구축하였습니다. 이러한 접근 방식은 전통적인 외래 키를 사용하는 방식 외에도 데이터베이스를 관리하는 데 필요한 다양한 역량을 키울 수 있는 기회를 제공하였습니다.

1. **데이터 모델링 지식**: 외래 키를 사용하지 않는 데이터베이스 설계를 통해 다양한 모델링 기법과 패턴에 대한 이해를 높일 수 있었습니다. 외래키를 왜 사용하는지에 대해 몸소 체험하게 되었습니다.
2. **데이터 무결성 유지 전략**: 외래 키를 사용하지 않는 상황에서도 데이터 무결성을 유지하기 위해 예외처리에 힘썼습니다. 이를 통해 데이터 관리의 전반적인 노하우를 향상시켰습니다.

### 외래 키(FK) 설정 없이 데이터베이스를 설계하면...?

#### 장점

1. **설계 단순화**: 외래 키 제약 조건이 없으므로 데이터베이스 구조가 단순해집니다.
2. **삽입 및 삭제 용이성**: 외래 키 제약 조건이 없기 때문에 관련된 테이블에 데이터를 추가하거나 삭제할 때 더 쉽습니다.

#### 단점

1. **데이터 무결성 위험**: 외래 키가 없으면 데이터 무결성을 유지하는 것이 어려워집니다. 예를 들어, 참조되지 않는 데이터나 중복 데이터가 발생할 수 있습니다.
2. **관계 관리 어려움**: 데이터베이스 간의 관계를 직접 관리해야 하므로 복잡성이 증가하며, 오류가 발생할 가능성이 높아집니다.
3. **성능 저하**: 외래 키를 사용하지 않으면 데이터 간의 관계를 직접 처리해야 하므로 쿼리 성능이 저하될 수 있습니다.

#### REST API 설계에 미치는 영향

1. **API 구현 복잡성 증가**: 외래 키 설정이 없으면 엔드포인트 간의 관계를 수동으로 처리해야 합니다. 이로 인해 API 구현이 복잡해질 수 있습니다.
2. **데이터 무결성 위험**: 데이터 무결성 문제로 인해 API의 결과가 부정확하거나 일관성이 없을 수 있습니다. 이는 클라이언트 측에서 추가적인 데이터 검증 작업이 필요하게 됩니다.
3. **유지 관리 어려움**: 외래 키 설정이 없는 데이터베이스는 유지 관리가 어렵습니다. 이로 인해 API의 유지 관리 비용이 증가할 수 있습니다.

### 3-1. DB (MySQL)

[ERD Cloud](https://www.erdcloud.com/d/9Dj4MrsP7rT4XK6sD)

![Springboot-library](https://user-images.githubusercontent.com/109801772/229270598-7bcfe4c5-aed4-4bac-8e20-ed7eba3e30ab.png)

### 3-2. REST API(with.Swagger)

#### Configuration

[MyRestConfig.java](https://github.com/Jimoou/BookLibrarySite-Backend/blob/develop/src/main/java/com/reactlibraryproject/springbootlibrary/Config/MyDataRestConfig.java)

이 코드는 Spring Data REST를 사용하여 RESTful API를 구성하는데 사용되는 설정 클래스입니다. MyDataRestConfig 클래스는 RepositoryRestConfigurer 인터페이스를 구현하여 [API의 동작을 정의하고 커스터마이징]합니다. [이 설정](#configuration-detail)을 통해 각 엔티티에 대한 API 엔드포인트의 동작을 제한하고 CORS(Cross-Origin Resource Sharing) 설정을 지정합니다.

이러한 설정을 통해 특정 도메인에서만 요청을 허용하고, 엔티티를 수정하거나 삭제하는 데 사용되는 HTTP 메서드를 비활성화함으로써 데이터의 무결성을 보호할 수 있습니다.

#### Configuration detail

<details>
<summary>접기/펼치기</summary>

- disableHttpMethods: HttpMethod 배열인 theUnsupportedActions에 지정된 HTTP 메서드를 사용하지 못하게 합니다. 여기서는 POST, DELETE, PUT, PATCH 메서드를 사용하지 못하게 설정하였습니다. 이렇게 하면 해당 엔드포인트에서 지원하지 않는 동작을 제한할 수 있습니다.
- CORS 설정: cors.addMapping을 사용하여 허용된 도메인에서 API 엔드포인트에 대한 요청을 허용합니다. 여기서는 theAllowedOrigins 변수에 저장된 도메인만 요청을 허용하도록 설정하였습니다.
- disableHttpMethods 메서드: 이 메서드는 도메인 타입에 대한 지정된 HTTP 메서드를 비활성화하는 역할을 합니다. forDomainType 메서드를 사용하여 특정 엔티티에 적용되도록 지정하고, withItemExposure와 withCollectionExposure를 사용하여 개별 엔티티와 컬렉션에 대해 비활성화할 메서드를 설정합니다.

</details>

#### API

<details>
<summary>리뷰 API</summary>

#### POST

`/api/reviews/secure`  
리뷰 남기기

#### GET

`/api/reviews/secure/user/book`  
리뷰 검증

</details>

<details>
<summary>코인 API</summary>

#### GET

`/api/coins/secure/history/using`  
코인 사용 내역 조회

#### GET

`/api/coins/secure/history/charge`  
코인 충전 내역

#### GET

`/api/coins/secure/count`  
유저의 보유 코인 수

</details>

<details>
<summary>장바구니 API</summary>

#### PUT

`/api/cart-items/secure/increase/item/amount`  
장바구니에서 책 수량 +

#### PUT

`/api/cart-items/secure/delete/item`  
장바구니에서 책 삭제

#### PUT

`/api/cart-items/secure/decrease/item/amount`  
장바구니에서 책 수량 -

#### PUT

`/api/cart-items/secure/add/item`  
장바구니에 책 추가

#### GET

`/api/cart-items/secure`  
유저의 장바구니 목록

</details>

<details>
<summary>관리자 API</summary>

#### PUT

`/api/admin/secure/increase/book/quantity`  
책의 대여 가능 권 수 +

#### PUT

`/api/admin/secure/decrease/book/quantity`  
책의 대여 가능 권 수 -

#### POST

`/api/admin/secure/add/book`  
책 추가

#### DELETE

`/api/admin/secure/delete/book`  
책 삭제

</details>

<details>
<summary>문의 API</summary>

#### PUT

`/api/messages/secure/admin/message`  
관리자의 답변 작성

#### POST

`/api/messages/secure/add/message`  
유저의 문의 작성

</details>

</details>

<details>
<summary>책 API</summary>

#### PUT

`/api/books/secure/return`  
책 반납

#### PUT

`/api/books/secure/renew/loan`  
대여 기간 연장

#### PUT

`/api/books/secure/checkout`  
책 대여

#### GET

`/api/books/secure/ischeckedout/byuser`  
유저가 현재 대여중인 책인지에 대한 검증

#### GET

`/api/books/secure/currentloans`  
유저의 현재 대여중인 책 목록

#### GET

`/api/books/secure/currentloans/count`  
유저가 현재 대여중인 책의 수

</details>

<details>
<summary>결제 API</summary>

#### POST

`/api/payment-histories/secure/confirm`  
결제 승인 API 호출

#### POST

`/api/payment-histories/secure/addpending`  
결제 승인 전 DB에 추가

#### GET

`/api/payment-histories/secure`  
결제 내역 조회

#### DELETE

`/api/payment-histories/secure/delete/fail`  
결제 실패 내역 삭제

</details>

## 4. 기술

### 4-1. TestCode (With.TDD)

프로젝트의 테스트 코드를 작성하면서 TDD(Test Driven Development) 접근법을 따랐습니다. 현재 테스트 커버리지는 50%로, 구현된 코드를 검증하는 목적으로 다양한 테스트 케이스를 작성했습니다.

프로젝트의 품질을 향상시키기 위해 테스트 커버리지를 지속적으로 높이려고 노력하고 있습니다. 향후 개발 과정에서 테스트 커버리지를 80% 이상으로 높이는 것을 목표로 하고 있습니다. 이를 통해 더욱 안정적이고 신뢰할 수 있는 프로젝트를 제공하려고 합니다.

<details>
<summary>테스트 커버리지</summary>

<img width="656" alt="스크린샷 2023-04-01 오후 5 02 19" src="https://user-images.githubusercontent.com/109801772/229273893-6fd6c180-c67a-4c5f-af65-c2d9e3c96f43.png">

</details>

<details>
<summary>테스트 결과</summary>

<img width="682" alt="스크린샷 2023-04-01 오후 5 03 21" src="https://user-images.githubusercontent.com/109801772/229273888-e3e866bb-3f4e-427a-bc38-f55e72f0af56.png">

</details>

### 4-2. Okta

<details>
<summary>Okta를 사용한 이유</summary>

Okta와 비슷한 다른 SSO 서비스로는 Auth0, Keycloak, OneLogin, Google Identity Platform 등이 있습니다. 이들 각각의 서비스는 고유한 기능과 특징을 가지고 있으며, 다양한 상황과 요구에 맞게 선택될 수 있습니다. Okta가 다른 SSO 서비스에 비해 더 나은 점은 다음과 같습니다.

1. **사용자 경험**: Okta는 사용자 친화적인 대시보드와 간편한 설정 과정을 제공합니다. 이로 인해 사용자가 쉽게 서비스를 구축하고 관리할 수 있습니다.

2. **다양한 통합**: Okta는 수많은 사전 구축된 통합을 제공하며, 다양한 웹 애플리케이션과 서비스와의 호환성을 확보합니다. 이를 통해 사용자는 별도의 개발 작업 없이 서드파티 서비스를 쉽게 연동할 수 있습니다.

3. **고급 보안 기능**: Okta는 다양한 보안 기능을 제공하며, 이러한 기능을 쉽게 구현할 수 있도록 지원합니다. 예를 들어, 다단계 인증(MFA), 비밀번호 정책, 사용자 그룹 관리 등의 기능이 있습니다.

4. **확장성**: Okta는 규모가 크거나 작은 조직에서도 사용할 수 있도록 확장성이 높습니다. 이를 통해 사용자는 조직의 성장에 따라 서비스를 쉽게 확장할 수 있습니다.

5. **지원 및 커뮤니티**: Okta는 뛰어난 고객 지원 및 커뮤니티를 제공합니다. 사용자는 문제가 발생했을 때 신속하게 도움을 받을 수 있으며, 다양한 경험을 공유하는 커뮤니티를 통해 지식을 얻을 수 있습니다.

</details>

### 4-3. TossPayments

<details>
<summary>Toss를 사용한 이유</summary>

Toss Payments와 비슷한 다른 결제 서비스로는 Stripe, PayPal, Iamport 등이 있습니다. 이들 각각의 서비스는 고유한 기능과 특징을 가지고 있으며, 다양한 상황과 요구에 맞게 선택될 수 있습니다. Toss Payments가 다른 결제 서비스에 비해 더 나은 점은 다음과 같습니다.

1. **국내 시장 적합성**: Toss Payments는 대한민국 시장에 맞춰진 결제 서비스로, 국내에서 가장 널리 사용되는 카드 및 결제 수단을 지원합니다. 이로 인해 사용자가 국내 시장에서 결제를 원활하게 진행할 수 있습니다.

2. **사용자 경험**: Toss Payments는 사용자 친화적인 결제 환경을 제공하며, 간편한 결제 프로세스를 통해 사용자의 결제 경험을 개선합니다.

3. **통합 및 개발 용이성**: Toss Payments는 API를 통해 손쉽게 통합할 수 있으며, 개발자들이 쉽게 결제 시스템을 구현할 수 있도록 지원합니다. 또한, 개발 가이드 및 예제 코드를 제공하여 개발의 효율성을 높입니다.

4. **안정성 및 보안**: Toss Payments는 안정적인 결제 서비스를 제공하며, 전문적인 보안 인증을 받은 서비스입니다. 이를 통해 사용자의 결제 데이터를 안전하게 보호할 수 있습니다.

5. **지원 및 커뮤니티**: Toss Payments는 뛰어난 고객 지원 및 커뮤니티를 제공합니다. 특히 디스코드 오픈 서버를 통해, 개발자, 사용자는 문제가 발생했을 때 신속하게 도움을 받을 수 있으며, 다양한 경험을 공유하는 커뮤니티를 통해 지식을 얻을 수 있습니다.

</details>

### 4-4. 의존성

- Java 17
- Spring Boot 2.7.9
- Spring Data JPA
- Spring Data REST
- Spring Boot Validation
- Okta Spring Boot Starter 2.1.6
- Springdoc OpenAPI UI 1.6.9
- JSON 20230227
- Lombok
- MySQL Connector/J

### 4-5. 스택

<div style="">

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Spring_Framework_Logo_2018.svg/1200px-Spring_Framework_Logo_2018.svg.png" alt="Spring Boot" width="100"/> <br>
<img src="https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fcc30ef61-cb1a-4636-9e39-5605b66aefee%2FTossPayments_Logo_Primary.png&blockId=b2f47d0c-d37b-4375-86a7-b16000619756&width=3600" width="100"/> <br>
<img src="https://raw.githubusercontent.com/swagger-api/swagger.io/wordpress/images/assets/SW-logo-clr.png" alt="Swagger" width="100"/>

<img src="https://www.okta.com/sites/default/files/Okta_Logo_BrightBlue_Medium-thumbnail.png" alt="Okta" width="100"/>

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/React-icon.svg/1200px-React-icon.svg.png" alt="React" width="50"/>
<img src="https://iconape.com/wp-content/png_logo_vector/typescript.png" alt="TypeScript" width="50"/>

</div>
