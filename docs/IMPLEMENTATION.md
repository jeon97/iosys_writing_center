# 구현 상세

## 기관별 인증 흐름 통합

기관마다 인증 API의 요청 형식과 SSO 사용 여부가 달랐습니다. 로그인 Controller에서 기관 코드를 직접 늘려가기보다 인증 방식을 분리하고, 공통 흐름에서는 결과만 사용하도록 구성하는 것이 핵심이었습니다.

```text
사용자 조회
  → 계정 상태와 실패 횟수 확인
  → 기관 설정에 맞는 인증 방식 선택
  → 인증 성공 시 실패 횟수 초기화
  → 권한과 사용자 정보로 세션 구성
  → 기존 중복 세션 교체
  → 동의 여부와 권한에 따라 화면 이동
```

공개 샘플에서는 외부 인증을 `AuthenticationProvider`, 중복 세션 처리를 `SessionRegistry`로 분리했습니다.

[AuthenticationService 코드](../samples/operations-core/src/main/java/com/portfolio/writing/auth/AuthenticationService.java)

## 접속 로그와 활동 로그 관리

화면 조회와 엑셀 다운로드가 서로 다른 조건을 사용하면 운영자가 확인하는 건수가 달라집니다. 검색 조건을 하나의 객체로 만들고 목록, 건수, 내보내기에 동일하게 전달했습니다.

로그 삭제는 임의 날짜가 아니라 보존 연도를 기준으로 경계 날짜를 계산하고 관리자 권한을 확인한 뒤 실행하도록 구성했습니다.

[AuditLogService 코드](../samples/operations-core/src/main/java/com/portfolio/writing/audit/AuditLogService.java)

## 웹 에디터 입력 처리

게시판 편집 화면에서는 제목, 본문, 첨부파일이 함께 전달됩니다. 화면의 필수 입력 검사만 신뢰하지 않고 서버에서도 다음 조건을 확인해야 합니다.

- 제목과 본문의 공백 입력 차단
- 제목 최대 길이 제한
- 허용된 첨부파일 확장자 검사
- 첨부파일 개수 제한

[ContentPolicy 코드](../samples/operations-core/src/main/java/com/portfolio/writing/content/ContentPolicy.java)

## 공개 코드 작성 기준

- 원본 클래스명, 메서드명, JSP와 쿼리를 복사하지 않았습니다.
- 기관 코드, 인증 주소, 사용자 권한 코드와 운영 데이터를 제거했습니다.
- 담당 기능의 처리 구조만 유지해 Java 17 코드로 새로 작성했습니다.
- 정상 흐름과 실패 조건을 단위 테스트로 검증합니다.

