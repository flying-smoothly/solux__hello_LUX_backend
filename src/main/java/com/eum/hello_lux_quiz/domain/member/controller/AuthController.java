package com.eum.hello_lux_quiz.domain.member.controller;

import com.eum.hello_lux_quiz.domain.member.dto.LoginRequest;
import com.eum.hello_lux_quiz.domain.member.dto.LoginResponse;
import com.eum.hello_lux_quiz.domain.member.dto.ProfileUpdateRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterRequest;
import com.eum.hello_lux_quiz.domain.member.dto.RegisterResponse;
import com.eum.hello_lux_quiz.domain.member.service.AuthService;
import com.eum.hello_lux_quiz.global.common.MessageResponse;
import com.eum.hello_lux_quiz.global.common.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** 개인정보 수정 */
    @PutMapping("/profile")
    public ResponseEntity<MessageResponse> updateProfile(@RequestBody ProfileUpdateRequest request) {
        authService.updateProfile(SecurityUtil.getCurrentEmail(), request);
        return ResponseEntity.ok(MessageResponse.of("수정 완료"));
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdraw() {
        authService.withdraw(SecurityUtil.getCurrentEmail());
        return ResponseEntity.ok(MessageResponse.of("탈퇴 완료"));
    }
}
