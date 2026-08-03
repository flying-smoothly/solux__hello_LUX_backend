package com.eum.hello_lux_quiz.domain.member.controller;

import com.eum.hello_lux_quiz.domain.member.dto.CheckIdResponse;
import com.eum.hello_lux_quiz.domain.member.dto.LoginRequest;
import com.eum.hello_lux_quiz.domain.member.dto.LoginResponse;
import com.eum.hello_lux_quiz.domain.member.dto.MeResponse;
import com.eum.hello_lux_quiz.domain.member.dto.ProfileUpdateRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterResponse;
import com.eum.hello_lux_quiz.domain.member.dto.RoleUpdateRequest;
import com.eum.hello_lux_quiz.domain.member.service.AuthService;
import com.eum.hello_lux_quiz.global.common.MessageResponse;
import com.eum.hello_lux_quiz.global.common.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 아이디 중복확인 */
    @GetMapping("/check-id")
    public ResponseEntity<CheckIdResponse> checkId(@RequestParam("user_id") String userId) {
        return ResponseEntity.ok(authService.checkId(userId));
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** 역할 설정 (회원가입 → 역할선택) */
    @PatchMapping("/role")
    public ResponseEntity<MessageResponse> updateRole(@Valid @RequestBody RoleUpdateRequest request) {
        authService.updateRole(SecurityUtil.getCurrentUserId(), request.role());
        return ResponseEntity.ok(MessageResponse.of("역할 설정 완료"));
    }

    /** 내 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        return ResponseEntity.ok(authService.getMe(SecurityUtil.getCurrentUserId()));
    }

    /** 개인정보 수정 */
    @PutMapping("/profile")
    public ResponseEntity<MessageResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        authService.updateProfile(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.ok(MessageResponse.of("수정 완료"));
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdraw() {
        authService.withdraw(SecurityUtil.getCurrentUserId());
        return ResponseEntity.ok(MessageResponse.of("탈퇴 완료"));
    }
}
