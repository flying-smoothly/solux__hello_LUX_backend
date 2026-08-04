package com.eum.hello_lux_quiz.domain.member.entity;

import com.eum.hello_lux_quiz.global.common.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원(공통) 엔티티. ERD 의 `회원` 테이블에 대응한다.
 * 아이디(user_id)를 기본키로 사용하며 role 에 따라 환자/보호자/의사로 구분된다.
 * 화면 흐름이 "회원가입 → 역할선택" 이므로 role 은 가입 시점엔 비어있을 수 있다(nullable).
 */
@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "user_pw", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone", length = 20) // 선택입력으로 변경
    private String phone;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Builder
    public Member(String userId, String password, Role role, String name, LocalDate birthDate, String phone) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.joinDate = LocalDate.now();
    }

    public void updateProfile(String name, LocalDate birthDate, String phone) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
