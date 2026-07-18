package com.solux.web.domain.member.entity;

import com.solux.web.global.common.Role;
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
 * 이메일을 기본키로 사용하며 role 에 따라 환자/보호자/의사로 구분된다.
 */
@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "user_email", nullable = false, length = 100)
    private String email;

    @Column(name = "user_pw", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Builder
    public Member(String email, String password, Role role, String name, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.birthDate = birthDate;
        this.joinDate = LocalDate.now();
    }

    public void updateProfile(String name, LocalDate birthDate) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
