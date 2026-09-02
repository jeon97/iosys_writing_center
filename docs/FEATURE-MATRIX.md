# 기능별 구현 근거

| 담당 영역 | 개발한 기능 | 구현 방식 | 공개 예제 |
|---|---|---|---|
| 로그인 | 기관별 외부 인증, 실패 횟수, 권한별 이동 | 외부 인증 Provider와 공통 로그인 정책 분리 | [AuthenticationService](../samples/operations-core/src/main/java/com/portfolio/writing/auth/AuthenticationService.java) |
| 세션 | 사용자 세션 구성, 중복 로그인 교체 | 계정별 활성 세션을 하나로 관리 | [SessionRegistry](../samples/operations-core/src/main/java/com/portfolio/writing/auth/SessionRegistry.java) |
| SSO | 기관 인증 응답, 권한별 이동, 세션 교체·종료 | 서명·만료 확인 후 허용 경로만 사용하고 기존 세션 만료 | [SsoSessionService](../samples/operations-core/src/main/java/com/portfolio/writing/auth/SsoSessionService.java) |
| 운영 로그 | 접속·활동 로그 검색, 페이징, 엑셀, 삭제 | 같은 검색 조건을 목록·건수·내보내기에 재사용 | [AuditLogService](../samples/operations-core/src/main/java/com/portfolio/writing/audit/AuditLogService.java) |
| 게시판 | 일반·도서 게시판 등록·수정, 웹 에디터 | 화면 검사와 별개로 서버 입력·첨부 정책 적용 | [ContentPolicy](../samples/operations-core/src/main/java/com/portfolio/writing/content/ContentPolicy.java) |
| 검색·리포트 | 학사 사용자·강좌 검색, 기관별 결과 화면 | 검색 팝업과 선택 결과 전달, 설정별 리포트 분기 | [기여 내역](CONTRIBUTIONS.md) |

