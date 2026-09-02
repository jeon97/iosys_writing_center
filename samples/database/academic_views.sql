-- 공개용 예제입니다. 실제 기관의 테이블명, 컬럼명, 데이터는 사용하지 않았습니다.

CREATE VIEW portfolio_academic_user_v AS
SELECT
    term.academic_year,
    term.semester_code,
    person.login_id,
    person.display_name,
    CASE person.person_type
        WHEN 'STUDENT' THEN 'S'
        WHEN 'PROFESSOR' THEN 'T'
    END AS user_role,
    CASE WHEN person.person_type = 'STUDENT' THEN enrollment.grade_level END AS grade_level
FROM sample_person person
JOIN sample_term term ON term.active = TRUE
LEFT JOIN sample_enrollment enrollment
    ON enrollment.student_id = person.person_id
   AND enrollment.term_id = term.term_id
WHERE person.active = TRUE
  AND person.person_type IN ('STUDENT', 'PROFESSOR');

CREATE VIEW portfolio_academic_lecture_v AS
SELECT
    lecture.external_course_code AS lecture_code,
    CONCAT(lecture.course_name, ' ', lecture.section_name) AS lecture_name,
    term.academic_year,
    term.semester_code,
    professor.login_id AS professor_login_id,
    professor.display_name AS professor_name
FROM sample_lecture lecture
JOIN sample_term term ON term.term_id = lecture.term_id
JOIN sample_person professor ON professor.person_id = lecture.professor_id
WHERE lecture.active = TRUE;

CREATE VIEW portfolio_academic_enrollment_v AS
SELECT
    lecture.external_course_code AS lecture_code,
    CONCAT(lecture.course_name, ' ', lecture.section_name) AS lecture_name,
    term.academic_year,
    term.semester_code,
    student.login_id AS student_login_id,
    student.display_name AS student_name
FROM sample_enrollment enrollment
JOIN sample_lecture lecture ON lecture.lecture_id = enrollment.lecture_id
JOIN sample_term term ON term.term_id = lecture.term_id
JOIN sample_person student ON student.person_id = enrollment.student_id
WHERE enrollment.status = 'ENROLLED'
  AND lecture.active = TRUE;

