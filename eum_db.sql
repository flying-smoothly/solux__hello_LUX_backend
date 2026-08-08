-- eum_db 전체 테이블 생성 SQL
-- 이 파일은 초기 ERD 설계 문서이며 실제 스키마가 아니다.

-- 1. 회원 (최상위 부모)
CREATE TABLE `회원` (
  `user_email` varchar(255) NOT NULL,
  `user_pw`    text         NOT NULL,
  `role`       varchar(50)  NOT NULL COMMENT 'patient / guardian / doctor',
  `name`       varchar(100) NOT NULL,
  `date`       date         NOT NULL,
  `join_date`  date         NOT NULL,
  PRIMARY KEY (`user_email`)
);

-- 2. 환자
CREATE TABLE `환자` (
  `p_code`       integer      NOT NULL AUTO_INCREMENT,
  `user_email`   varchar(255) NOT NULL,
  `patient_code` varchar(6)   NOT NULL UNIQUE COMMENT,
  PRIMARY KEY (`p_code`),
  CONSTRAINT `FK_환자_회원`
    FOREIGN KEY (`user_email`) REFERENCES `회원` (`user_email`)
);

-- 3. 보호자
CREATE TABLE `보호자` (
  `p_code`     integer      NOT NULL,
  `user_email` varchar(255) NOT NULL,
  CONSTRAINT `FK_보호자_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`),
  CONSTRAINT `FK_보호자_회원`
    FOREIGN KEY (`user_email`) REFERENCES `회원` (`user_email`)
);

-- 4. 의사
CREATE TABLE `의사` (
  `user_email` varchar(255) NOT NULL,
  `report`     text         NOT NULL,
  PRIMARY KEY (`user_email`),
  CONSTRAINT `FK_의사_회원`
    FOREIGN KEY (`user_email`) REFERENCES `회원` (`user_email`)
);

-- 5. 환자_프로필
CREATE TABLE `환자_프로필` (
  `p_code`      integer      NOT NULL,
  `name`        varchar(100) NOT NULL,
  `birth_date`  date         NOT NULL,
  `gender`      varchar(10)  NOT NULL,
  `diagnosis`   varchar(255) NOT NULL,
  `personality` text         NOT NULL,
  `style`       text         NOT NULL,
  CONSTRAINT `FK_환자프로필_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`)
);

-- 6. 삶의DB
CREATE TABLE `삶의DB` (
  `memory_id`   integer      NOT NULL AUTO_INCREMENT,
  `p_code`      integer      NOT NULL,
  `title`       text         NOT NULL,
  `record_date` date         NOT NULL,
  `place`       text         NOT NULL,
  `likes`       text         NOT NULL COMMENT '좋아하는 것 (like는 예약어라 likes로 변경)',
  `job`         text         NOT NULL,
  `hometown`    varchar(255) NOT NULL,
  `family`      text         NOT NULL,
  PRIMARY KEY (`memory_id`),
  CONSTRAINT `FK_삶의DB_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`)
);

-- 7. 세분화
CREATE TABLE `세분화` (
  `event_id`  integer      NOT NULL AUTO_INCREMENT,
  `memory_id` integer      NOT NULL,
  `event`     text         NOT NULL,
  `photo_url` text         NULL,
  `category`  varchar(50)  NOT NULL COMMENT '기쁨, 슬픔, 불안 등',
  PRIMARY KEY (`event_id`),
  CONSTRAINT `FK_세분화_삶의DB`
    FOREIGN KEY (`memory_id`) REFERENCES `삶의DB` (`memory_id`)
);

-- 8. 퀴즈세트
CREATE TABLE `퀴즈세트` (
  `set_id`      integer NOT NULL AUTO_INCREMENT,
  `p_code`      integer NOT NULL,
  `start_time`  time    NOT NULL,
  `end_time`    time    NOT NULL,
  `anti_time`   integer NULL,
  `total_score` integer NOT NULL,
  PRIMARY KEY (`set_id`),
  CONSTRAINT `FK_퀴즈세트_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`)
);

-- 9. 퀴즈응시
CREATE TABLE `퀴즈응시` (
  `quiz_num`      integer      NOT NULL AUTO_INCREMENT,
  `set_id`        integer      NOT NULL,
  `p_code`        integer      NOT NULL,
  `score`         integer      NOT NULL COMMENT '일일 점수 계산',
  `quiz_category` varchar(50)  NOT NULL COMMENT 'text / photo',
  `level`         integer      NOT NULL,
  `quiz_comment`  text         NULL COMMENT '텍스트 기반 문제',
  `quiz_photo`    text         NULL COMMENT '사진 기반 문제',
  `answer`        text         NOT NULL,
  PRIMARY KEY (`quiz_num`),
  CONSTRAINT `FK_퀴즈응시_퀴즈세트`
    FOREIGN KEY (`set_id`) REFERENCES `퀴즈세트` (`set_id`)
);

-- 10. 퀴즈_결과
CREATE TABLE `퀴즈_결과` (
  `result_id`     integer NOT NULL AUTO_INCREMENT,
  `set_id`        integer NOT NULL,
  `p_code`        integer NOT NULL,
  `date`          date    NOT NULL,
  `total_count`   integer NOT NULL,
  `correct_count` integer NOT NULL,
  `hint`          integer NOT NULL,
  `caculate`      text    NOT NULL COMMENT '7~30일 단위 변화 계산, 기간별 추세, 급격한 변화 감지',
  PRIMARY KEY (`result_id`),
  CONSTRAINT `FK_퀴즈결과_퀴즈세트`
    FOREIGN KEY (`set_id`) REFERENCES `퀴즈세트` (`set_id`),
  CONSTRAINT `FK_퀴즈결과_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`)
);

-- 11. 퀴즈_피드백
CREATE TABLE `퀴즈_피드백` (
  `feedback_id`      integer NOT NULL AUTO_INCREMENT,
  `set_id`           integer NOT NULL,
  `feedback_content` text    NOT NULL,
  `created_at`       date    NOT NULL,
  PRIMARY KEY (`feedback_id`),
  CONSTRAINT `FK_퀴즈피드백_퀴즈세트`
    FOREIGN KEY (`set_id`) REFERENCES `퀴즈세트` (`set_id`)
);

-- 12. 힌트
CREATE TABLE `힌트` (
  `hint_id`   integer NOT NULL AUTO_INCREMENT,
  `p_code`    integer NOT NULL,
  `event_id`  integer NOT NULL,
  `set_id`    integer NOT NULL,
  `hint_text` text    NOT NULL,
  PRIMARY KEY (`hint_id`),
  CONSTRAINT `FK_힌트_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`),
  CONSTRAINT `FK_힌트_퀴즈세트`
    FOREIGN KEY (`set_id`) REFERENCES `퀴즈세트` (`set_id`)
);

-- 13. 상태_메모
CREATE TABLE `상태_메모` (
  `memo_id`    integer      NOT NULL AUTO_INCREMENT,
  `p_code`     integer      NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `p_content`  text         NOT NULL,
  `p_change`   text         NOT NULL,
  `created_at` date         NOT NULL,
  `update_at`  date         NOT NULL,
  PRIMARY KEY (`memo_id`),
  CONSTRAINT `FK_상태메모_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`),
  CONSTRAINT `FK_상태메모_회원`
    FOREIGN KEY (`user_email`) REFERENCES `회원` (`user_email`)
);

-- 14. 의사_코멘트
CREATE TABLE `의사_코멘트` (
  `comment_id`   integer      NOT NULL AUTO_INCREMENT,
  `p_code`       integer      NOT NULL,
  `user_email`   varchar(255) NOT NULL,
  `text`         text         NOT NULL,
  `comment_date` date         NOT NULL,
  PRIMARY KEY (`comment_id`),
  CONSTRAINT `FK_의사코멘트_환자`
    FOREIGN KEY (`p_code`) REFERENCES `환자` (`p_code`),
  CONSTRAINT `FK_의사코멘트_회원`
    FOREIGN KEY (`user_email`) REFERENCES `회원` (`user_email`)
);
