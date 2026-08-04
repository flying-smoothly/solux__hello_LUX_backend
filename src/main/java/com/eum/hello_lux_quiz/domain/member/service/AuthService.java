package com.eum.hello_lux_quiz.domain.member.service;

import com.eum.hello_lux_quiz.domain.member.dto.CheckIdResponse;
import com.eum.hello_lux_quiz.domain.member.dto.LoginRequest;
import com.eum.hello_lux_quiz.domain.member.dto.LoginResponse;
import com.eum.hello_lux_quiz.domain.member.dto.MeResponse;
import com.eum.hello_lux_quiz.domain.member.dto.ProfileUpdateRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterResponse;
import com.eum.hello_lux_quiz.domain.member.entity.Member;
import com.eum.hello_lux_quiz.domain.member.repository.MemberRepository;
import com.eum.hello_lux_quiz.domain.patient.repository.PatientRepository;
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
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입. 아이디 중복을 검사하고 비밀번호를 암호화하여 저장한다.
     * 역할(role)은 가입 시점엔 비워두고 이후 {@link #updateRole} 로 설정한다.
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (memberRepository.existsByUserId(request.userId())) {
            throw new CustomException(ErrorCode.USER_ID_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .userId(request.userId())
                .password(passwordEncoder.encode(request.userPw()))
                .role(null) // 가입 시점엔 역할 미설정, 이후 updateRole 로 지정
                .name(request.name())
                .phone(request.phone())
                .build();

        memberRepository.save(member);
        return RegisterResponse.from(member);
    }

    /**
     * 아이디 중복확인. 사용 가능하면 available=true.
     */
    public CheckIdResponse checkId(String userId) {
        return new CheckIdResponse(!memberRepository.existsByUserId(userId));
    }

    /**
     * 로그인. 아이디/비밀번호를 검증하고 JWT 토큰을 발급한다. role 은 선택 전이면 null
     */
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByUserId(request.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.userPw(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String role = member.getRole() != null ? member.getRole().getValue() : null;
        String token = jwtTokenProvider.createToken(member.getUserId(), role);
        return new LoginResponse(token, role, member.getUserId(), member.getName());
    }

    /**
     * 역할 설정. 회원가입 이후 역할선택 화면에서 호출한다.
     *      */
    @Transactional
    public void updateRole(String userId, String role) {
        Member member = getMember(userId);
        member.updateRole(parseRole(role));
    }

    /**
     * 현재 로그인한 회원 정보 조회. 환자로 등록되어 있으면 연동용 코드(p_code)를 함께 반환한다.
     */
    public MeResponse getMe(String userId) {
        Member member = getMember(userId);
        String patientCode = patientRepository.findByUserId(userId)
                .map(patient -> patient.getPatientCode())
                .orElse(null);
        return MeResponse.of(member, patientCode);
    }

    /**
     * 개인정보 수정. 현재 비밀번호(current_pw) 확인 후 전달된 필드만 반영하며,
     * 새 비밀번호(user_pw)가 있으면 재암호화한다.
     */
    @Transactional
    public void updateProfile(String userId, ProfileUpdateRequest request) {
        Member member = getMember(userId);

        if (!passwordEncoder.matches(request.currentPw(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        member.updateProfile(request.name(), request.birthDate(), request.phone());
        if (request.userPw() != null && !request.userPw().isBlank()) {
            member.updatePassword(passwordEncoder.encode(request.userPw()));
        }
    }

    /**
     * 회원 탈퇴.
     */
    @Transactional
    public void withdraw(String userId) {
        Member member = getMember(userId);
        memberRepository.delete(member);
    }

    private Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
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
