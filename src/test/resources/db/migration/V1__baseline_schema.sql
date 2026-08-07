-- V1: 공유 DB 베이스라인 스키마

-- 규칙: 이 파일은 절대 수정하지 않는다. 스키마 변경은 항상 새 V{n}__*.sql 을 추가한다.

CREATE TABLE member (
    user_id    VARCHAR(20)  NOT NULL,
    user_pw    VARCHAR(255) NOT NULL,
    role       ENUM ('DOCTOR','GUARDIAN','PATIENT'),
    name       VARCHAR(50)  NOT NULL,
    birth_date DATE         NOT NULL,
    phone      VARCHAR(20),
    join_date  DATE         NOT NULL,
    PRIMARY KEY (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Patient 의 @TableGenerator(p_code 1001 부터 발급) 백업 테이블
CREATE TABLE p_code_seq (
    seq_name  VARCHAR(255) NOT NULL,
    seq_value BIGINT,
    PRIMARY KEY (seq_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO p_code_seq (seq_name, seq_value) VALUES ('p_code', 1000);

CREATE TABLE patient (
    p_code                  INTEGER      NOT NULL,
    user_id                 VARCHAR(100) NOT NULL,
    patient_code            VARCHAR(6)   NOT NULL,
    gender                  VARCHAR(10)  NOT NULL,
    diagnosis               VARCHAR(100) NOT NULL,
    personality             TEXT,
    speech_style            TEXT,
    cognitive_support_level VARCHAR(10),
    guardian_companion      BIT,
    patient_status          VARCHAR(50),
    PRIMARY KEY (p_code),
    CONSTRAINT uk_patient__patient_code UNIQUE (patient_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE patient_profile (
    p_code              INTEGER NOT NULL,
    name                VARCHAR(255),
    style               VARCHAR(255),
    patient_status      VARCHAR(255),
    cognitive_level     VARCHAR(255),
    is_guardian_present BIT,
    voice_setting       JSON,
    personality         VARCHAR(255),
    PRIMARY KEY (p_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE patient_daily_status (
    status_id         BIGINT  NOT NULL AUTO_INCREMENT,
    p_code            INTEGER NOT NULL,
    record_date       DATE    NOT NULL,
    health_condition  VARCHAR(20),
    sleep_status      VARCHAR(20),
    meal_status       VARCHAR(20),
    pain_status       VARCHAR(20),
    mood_status       VARCHAR(20),
    cognitive_changes TEXT,
    created_at        DATE    NOT NULL,
    updated_at        DATE    NOT NULL,
    PRIMARY KEY (status_id),
    CONSTRAINT uk_patient_daily_status__p_code_record_date UNIQUE (p_code, record_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE guardian (
    guardian_id BIGINT       NOT NULL AUTO_INCREMENT,
    p_code      INTEGER      NOT NULL,
    user_id     VARCHAR(100) NOT NULL,
    PRIMARY KEY (guardian_id),
    CONSTRAINT uk_guardian__p_code_user_id UNIQUE (p_code, user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE status_memo (
    memo_id       BIGINT       NOT NULL AUTO_INCREMENT,
    p_code        INTEGER      NOT NULL,
    user_id       VARCHAR(100) NOT NULL,
    record_date   DATE         NOT NULL,
    health_status VARCHAR(20),
    sleep_status  VARCHAR(20),
    meal_status   VARCHAR(20),
    pain_status   VARCHAR(20),
    mood_status   VARCHAR(20),
    behaviors     TEXT,
    need_referral BIT          NOT NULL,
    content       TEXT,
    created_at    DATE         NOT NULL,
    updated_at    DATE         NOT NULL,
    PRIMARY KEY (memo_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE doctor_patient (
    doctor_patient_id BIGINT       NOT NULL AUTO_INCREMENT,
    p_code            INTEGER      NOT NULL,
    user_id           VARCHAR(100) NOT NULL,
    PRIMARY KEY (doctor_patient_id),
    CONSTRAINT uk_doctor_patient__p_code_user_id UNIQUE (p_code, user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE doctor_report (
    report_id  BIGINT       NOT NULL AUTO_INCREMENT,
    p_code     INTEGER      NOT NULL,
    user_id    VARCHAR(100) NOT NULL,
    report     TEXT         NOT NULL,
    created_at DATE         NOT NULL,
    updated_at DATE         NOT NULL,
    PRIMARY KEY (report_id),
    CONSTRAINT uk_doctor_report__p_code_user_id UNIQUE (p_code, user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE patient_quiz_level (
    level_id  BIGINT      NOT NULL AUTO_INCREMENT,
    p_code    INTEGER     NOT NULL,
    quiz_type VARCHAR(20) NOT NULL,
    level     INTEGER     NOT NULL,
    PRIMARY KEY (level_id),
    CONSTRAINT uk_patient_quiz_level__p_code_quiz_type UNIQUE (p_code, quiz_type)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 삶의db (
    memory_id   INTEGER      NOT NULL AUTO_INCREMENT,
    p_code      INTEGER      NOT NULL,
    title       TEXT         NOT NULL,
    record_date DATE         NOT NULL,
    place       TEXT         NOT NULL,
    likes       TEXT         NOT NULL,
    job         TEXT         NOT NULL,
    hometown    VARCHAR(255) NOT NULL,
    family      TEXT         NOT NULL,
    PRIMARY KEY (memory_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 세분화 (
    event_id  INTEGER      NOT NULL AUTO_INCREMENT,
    memory_id INTEGER      NOT NULL,
    event     TEXT         NOT NULL,
    photo_url TEXT,
    category  VARCHAR(255) NOT NULL,
    PRIMARY KEY (event_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 퀴즈세트 (
    set_id      INTEGER NOT NULL AUTO_INCREMENT,
    p_code      INTEGER NOT NULL,
    start_time  TIME(0),
    end_time    TIME(0),
    anti_time   INTEGER,
    total_score INTEGER NOT NULL,
    PRIMARY KEY (set_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 퀴즈응시 (
    quiz_num      INTEGER     NOT NULL,
    set_id        INTEGER     NOT NULL,
    p_code        INTEGER     NOT NULL,
    score         INTEGER,
    quiz_category VARCHAR(50) NOT NULL,
    level         INTEGER     NOT NULL,
    quiz_comment  TEXT,
    quiz_photo    TEXT,
    answer        TEXT        NOT NULL,
    options       TEXT,
    hints         TEXT,
    PRIMARY KEY (p_code, quiz_num, set_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 퀴즈_결과 (
    result_id         INTEGER NOT NULL AUTO_INCREMENT,
    set_id            INTEGER NOT NULL,
    p_code            INTEGER NOT NULL,
    date              DATE    NOT NULL,
    total_count       INTEGER NOT NULL,
    correct_count     INTEGER NOT NULL,
    hint              INTEGER NOT NULL,
    caculate          TEXT    NOT NULL,
    success_rate      INTEGER,
    hint_used         BIT,
    avg_response_time INTEGER,
    health_status     VARCHAR(255),
    sleep_status      VARCHAR(255),
    emotion_status    VARCHAR(255),
    PRIMARY KEY (result_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 퀴즈_피드백 (
    feedback_id      INTEGER NOT NULL AUTO_INCREMENT,
    set_id           INTEGER NOT NULL,
    feedback_content TEXT    NOT NULL,
    created_at       DATE    NOT NULL,
    PRIMARY KEY (feedback_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE 힌트 (
    hint_id   BIGINT  NOT NULL AUTO_INCREMENT,
    p_code    BIGINT  NOT NULL,
    event_id  BIGINT  NOT NULL,
    set_id    BIGINT  NOT NULL,
    quiz_num  INTEGER,
    hint_text TEXT    NOT NULL,
    PRIMARY KEY (hint_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;