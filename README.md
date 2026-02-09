# Catch-a-Bite

**실시간 음식 주문 및 배달 통합 관리 플랫폼**

## 프로젝트 소개
**Catch-a-Bite**는 현재 배달 시장의 구조적 문제점인 과도한 중개 수수료와 독과점 폐해를 해결하기 위해 기획된 배달 서비스 플랫폼입니다.

우리는 다음과 같은 가치를 제공하는 투명하고 효율적인 대안 플랫폼을 지향합니다:
* **점주님**에게는 실질적인 마진 보장
* **라이더**에게는 공정한 운임 제공
* **소비자**에게는 합리적인 가격과 신뢰할 수 있는 서비스 제공

## 프로젝트 개요
* **프로젝트명:** Catch-a-Bite
* **개발 기간:** 2025.12.17 ~ 2026.02.02
* **서비스 대상:** 일반 사용자(고객), 가맹점주, 배달 라이더

## 기술 스택 (Tech Stack)

| 구분 | 상세 기술 |
| :--- | :--- |
| **Backend** | Java (JDK 17)<br>Spring Boot (Web, Security, JPA)<br>Gradle (Build Tool) |
| **Database** | MariaDB<br>JPA / Hibernate (ORM) |
| **Frontend & Client** | React + Vite (Web Interface) |
| **외부 연동 (Integrations)** | PortOne (PG사 결제 연동) |

## 시스템 아키텍처
본 프로젝트는 **MVC (Model-View-Controller)** 패턴을 기반으로 계층을 분리하여 유지보수성과 확장성을 확보했습니다.

* **Controller Layer:** HTTP 요청 처리 및 엔드포인트 매핑 (예: AuthController, OrderController)
* **Service Layer:** 핵심 비즈니스 로직 수행 (예: OrderService, PaymentService, SettlementService)
* **Repository Layer:** JPA 및 Native Query를 활용한 데이터 관리
* **DTO (Data Transfer Objects):** 계층 간 안전하고 명확한 데이터 전송
* **Security & Auth:** Spring Security 기반의 세션 관리와 사용자 유형(User, Owner, Rider)별 RBAC(Role-Based Access Control) 권한 분리 구현

## 주요 기능
사용자-점주-라이더 간의 실시간 데이터 연동이 가능한 통합 웹앱

### 고객 (App User)
* **계정 관리:** 회원가입/로그인, 프로필 수정, 배송지(주소록) 관리
* **가게 탐색:** 카테고리/위치 기반 가게 검색, 메뉴 및 가게 정보 조회
* **주문 시스템:** 실시간 장바구니 관리, 옵션 선택 및 주문 접수
* **결제:** PortOne API를 연동한 안전한 결제 처리
* **활동:** 주문 내역 조회, 리뷰 및 별점 작성

### 가맹점주 (Store Owner)
* **매장 운영:** 영업 상태(Open/Close) 관리, 가게 정보 수정
* **메뉴 관리:** 메뉴 카테고리, 메뉴 아이템, 추가 옵션 생성/수정/삭제
* **주문 접수:** 실시간 주문 수락/거절, 조리 예상 시간 설정
* **정산/매출:** 정산 내역 조회, 매출 및 거래 내역 분석

### 라이더 (Deliverer)
* **배달 수행:** 배달 콜 목록 조회, 배차 수락/거절 로직
* **경로 안내:** 픽업 및 배달 완료 상태 업데이트, 위치 기반 로직
* **수익 관리:** 배달 건별 운임 확인 및 정산 내역 조회

## 데이터베이스 설계 (ERD)
사용자, 주문, 배달 간의 복잡한 관계를 효율적으로 처리하기 위해 정규화된 데이터베이스를 설계했습니다.

* **사용자 그룹:** APP_USER, STORE_OWNER, DELIVERER
* **사용자 정보:** ADDRESS, FAVORITE_STORE, REVIEW
* **카트:** CART, CART_ITEM, CART_ITEM_OPTION
* **주문내역:** STORE_ORDER, ORDER_ITEM, ORDER_ITEM_OPTION
* **매장 데이터:** STORE, STORE_IMAGE, MENU, MENU_CATEGORY, MENU_OPTION_GROUP, MENU_OPTION, MENU_IMAGE, REVIEW_REPLY
* **재무/정산:** PAYMENT, OWNER_SETTLEMENT, DELIVERER_SETTLEMENT, DELIVERER_FEE_RULE

*(상세 스키마는 문서 내 구글 드라이브 문서함 내 1.5 ERD.xls에서 확인 가능)*

## API 명세
RESTful 원칙에 따라 설계된 주요 API 엔드포인트는 다음과 같습니다.

| 도메인 | 기본 경로 | 설명 |
| :--- | :--- | :--- |
| **인증 (Auth)** | /api/v1/auth | 로그인, 회원가입, 세션 관리 |
| **사용자 (User)** | /api/v1/appusers | 사용자 프로필 및 개인화 기능 |
| **가맹점주 (Owner)** | /api/v1/owner | 매장 목록, 메뉴 상세 조회 |
| **라이더 (Deliverer)** | /api/v1/riders | 라이더 배차 및 배달 상태 관리 |
| **결제 (Payment)** | /api/payments | 결제 검증 및 상세 내역 |

## 팀원 소개
* **[김정호](https://junghokim1031.github.io/portfolio/):** PM (Project Manager), 서기 및 통합담당자
* **[박성철](https://ps3542.github.io/portfolio/):** PL (Project Lead)
* **[이주희](https://juhee121.github.io/portfolio/):** DBA (Database Administrator)
* **[이주호](https://jhlee002.github.io/portfolio/):** 라이더 도메인 구현
* **[김진덕](https://cave8026.github.io/portfolio/):** 인증 및 공통 모듈
