# 🔐 jwt-auth-assignment

본 프로젝트는 백엔드 직무 인턴 지원을 위한 **JWT 기반 인증 시스템 구현 과제**입니다.

---

## 📑 목차

- [🕰️ 프로젝트 진행 기간](#-프로젝트-진행-기간)
- [🐣 개발자 소개](#-개발자-소개)
- [💡 프로젝트 목적](#-프로젝트-목적)
- [📌 기술 스택](#-기술-스택)
- [✅ 주요 기능](#-주요-기능)
- [🔐 인증 및 권한](#-인증-및-권한)
- [🧪 테스트 구성](#-테스트-구성)
- [🚀 배포](#-배포)
- [📡 API 명세](#-api-명세)
- [📨 문의 사항](#-문의-사항)

---

## 🕰️ 프로젝트 진행 기간
**2025. 04. 18. (금) ~ 2025. 04. 20. (일)**

✔️ 개발 기간: 2025. 04. 18. (금) ~ 2025. 04. 19. (토)

✔️ 배포 기간: 2025. 04. 19. (토) ~ 2025. 04. 20. (일)

---

## 🐣 개발자 소개
<img src="https://flat-argument-d72.notion.site/image/attachment%3A299ccf73-f33a-4a4f-8a1c-22cfb4761c1d%3Ayujin.png?table=block&id=1db7faba-9f3f-801a-a2dc-d5aea3f290d5&spaceId=6d2b4374-e79b-4036-a9cd-432b0325afd7&width=1420&userId=&cache=v2" alt="title" width="300"/>

### **Zin | 강유진**
- [Git](https://github.com/YJ-Kkang)
- [블로그](https://velog.io/@yjkang/posts)
- [유튜브](https://www.youtube.com/@ZenithOfZin)
- [Email](kyujin995@gmail.com)

---

## 💡 프로젝트 목적

- 회원가입 및 로그인 기능
- JWT 발급 및 인증
- 관리자 권한 제어 기능
- EC2에 배포 및 운영

---

## 📌 기술 스택

- Java 17
- Spring Boot 3.2.5
- Spring Security, JWT
- H2 In-Memory Database
- Swagger / OpenAPI
- AWS EC2
- GitHub Actions (main 브랜치 push 시 EC2에 자동 배포되도록 CI/CD 구성 준비 중)

---

## ✅ 주요 기능

| 기능 | 설명 |
|------|------|
| 회원가입 | 사용자/관리자 가입 가능 |
| 로그인 | JWT 토큰 발급 |
| 관리자 권한 부여 | 특정 유저에게 ADMIN 역할 부여 |
| 접근 제어 | 사용자/관리자 권한에 따라 API 접근 제한 |
| Swagger | `/swagger-ui.html`에서 API 테스트 가능 |
| 예외 응답 | 일관된 Error DTO 포맷 제공 |

---

## 🔐 인증 및 권한

- JWT에 사용자 ID, 권한, 만료시간 포함
- 모든 보호 API는 `Authorization: Bearer <token>` 필요
- 토큰 미포함/만료/서명 오류 시 401 Unauthorized
- 권한 부족 시 403 Forbidden 응답

---

## 🧪 테스트 구성

### ✔️ 단위 테스트 (JUnit + Mockito)
- `@DataJpaTest`: `Entity`, `Repository`에 대한 DB 저장/조회 검증
- `@ExtendWith(MockitoExtension.class)`: `Service`, `Controller` 계층의 단위 테스트

  | 테스트 대상 | 검증 내용 |
  |-------------|-----------|
  | **UserEntityTest** | 유저 저장/변경 실패 조건 (null 필드 등), 기본값 확인 |
  | **UserRepositoryTest** | 이메일 중복 여부, 유저 존재 여부 등 JPA 쿼리 검증 |
  | **UserServiceImplTest** | 회원가입/로그인/권한 부여 로직 검증, 예외 처리 |
  | **UserControllerTest** | HTTP 요청에 대한 응답 형식, 상태 코드, 예외 처리 등 |

### ✔️ 통합 테스트
- 실제 API 요청/응답 흐름 검증
- JWT 인증 흐름 테스트 포함
- `인증 없이 보호 API 접근` 시 `401`, `권한 부족` 시 `403` 응답 확인 

| API | 검증 항목 |
|-----|-----------|
| `/api/users/signup` | 회원가입 성공, 중복 이메일 예외 |
| `/api/signin` | 로그인 성공 및 JWT 반환, 잘못된 정보 예외 |
| `/api/my-informations` | 토큰 없이 접근 → 401, 올바른 토큰 → 성공 응답 |
| `/api/admins/users/{id}/roles` | 관리자 권한 부여, 권한 부족 → 403, JWT 누락 → 401 |


### ✔️ 클래스 기준 96% 커버리지 달성
![커버리지 표](https://flat-argument-d72.notion.site/image/attachment%3A793f5bfd-7532-4919-a19a-be89e05a32d8%3Aimage.png?table=block&id=1db7faba-9f3f-80af-91af-dbb9b1bb1ab5&spaceId=6d2b4374-e79b-4036-a9cd-432b0325afd7&width=2000&userId=&cache=v2)

---

## 🚀 배포

- AWS EC2 (Ubuntu 22.04, OpenJDK 17)
- `scp`로 `.jar` 업로드 후 수동 실행:
  ```bash
  java -jar jwt-auth-0.0.1-SNAPSHOT.jar
  ```
- 보안 그룹: 포트 8080 오픈

---

## 📡 API 명세

-   ✔️ **Swagger 문서**:  
  [http://ec2-3-39-22-53.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui.html](http://ec2-3-39-22-53.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui.html)


-  ✔️ **에러 응답 구조**

```json
{
   "error": {
              "code": "ERROR_CODE",
              "message": "에러 메시지입니다."
   }
}
```

-  ✔️ **API 명세서**
![api 표](https://flat-argument-d72.notion.site/image/attachment%3A7d1e69ad-55ec-47a0-bac1-d11e6bbb2185%3Aimage.png?table=block&id=1db7faba-9f3f-80a0-8b82-d0f04b90165a&spaceId=6d2b4374-e79b-4036-a9cd-432b0325afd7&width=2000&userId=&cache=v2)
![api 표2](https://flat-argument-d72.notion.site/image/attachment%3A06793c3a-9bd1-4267-b4b6-23170b3465bc%3Aimage.png?table=block&id=1db7faba-9f3f-800c-8fe9-dbde42123b76&spaceId=6d2b4374-e79b-4036-a9cd-432b0325afd7&width=2000&userId=&cache=v2)
![api 표3](https://flat-argument-d72.notion.site/image/attachment%3Afc479185-706d-473b-b866-c853162615c6%3Aimage.png?table=block&id=1db7faba-9f3f-80a0-9064-d0596773f9b5&spaceId=6d2b4374-e79b-4036-a9cd-432b0325afd7&width=2000&userId=&cache=v2)

---

## 📨 문의 사항
✔️ `Zin` : kyujin995@gmail.com