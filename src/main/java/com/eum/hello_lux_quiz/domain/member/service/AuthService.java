package com.eum.hello_lux_quiz.domain.member.service;

import com.eum.hello_lux_quiz.domain.member.dto.LoginRequest;
import com.eum.hello_lux_quiz.domain.member.dto.LoginResponse;
import com.eum.hello_lux_quiz.domain.member.dto.ProfileUpdateRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterResponse;
import com.eum.hello_lux_quiz.domain.member.entity.Member;
import com.eum.hello_lux_quiz.domain.member.repository.MemberRepository;
import com.eum.hello_lux_quiz.global.common.Role;
import com.eum.hello_lux_quiz.global.exception.CustomException;
import com.eum.hello_lux_quiz.global.exception.ErrorCode;
import com.eum.hello_lux_quiz.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입. 이메일 중복을 검사하고 비밀번호를 암호화하여 저장한다.
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.userEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role role = parseRole(request.role());

        Member member = Member.builder()
                .email(request.userEmail())
                .password(passwordEncoder.encode(request.userPw()))
                .role(role)
                .name(request.name())
                .birthDate(request.birthDate())
                .build();

        memberRepository.save(member);
        return RegisterResponse.from(member);
    }

    /**
     * 로그인. 이메일/비밀번호를 검증하고 JWT 토큰을 발급한다.
     */
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.userEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.userPw(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().getValue());
        return new LoginResponse(token, member.getRole().getValue(), member.getEmail());
    }

    /**
     * 개인정보 수정. 전달된 필드만 반영하며 비밀번호는 재암호화한다.
     */
    @Transactional
    public void updateProfile(String email, ProfileUpdateRequest request) {
        Member member = getMember(email);
        member.updateProfile(request.name(), request.birthDate());
        if (request.userPw() != null && !request.userPw().isBlank()) {
            member.updatePassword(passwordEncoder.encode(request.userPw()));
        }
    }

    /**
     * 회원 탈퇴.
     */
    @Transactional
    public void withdraw(String email) {
        Member member = getMember(email);
        memberRepository.delete(member);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Role parseRole(String role) {
        try {
            return Role.from(role);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }
    }
}
