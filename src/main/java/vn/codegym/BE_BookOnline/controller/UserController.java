package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.GoogleLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UpdateUserRequest;
import vn.codegym.BE_BookOnline.dto.request.UserLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UserRegisterRequest;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.service.UserService;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request){
            userService.registerUser(request);
            return ResponseEntity.ok("Đăng ký tài khoản thành công! Vui lòng kiểm tra email. ");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request){
        AuthResponse response = userService.loginWithLocal(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String token){
        userService.verifyAccount(token);
        return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công");
    }
    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request){
        AuthResponse response = userService.loginWithGoogle(request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update-profile")
    public ResponseEntity<UpdateUserResponse> updateProfile(@Valid @RequestBody UpdateUserRequest request){
        String email = getEmailFromAuthContext();
        UpdateUserResponse response = userService.updateUser(email, request);
        return ResponseEntity.ok(response);
    }
    private String getEmailFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Người dùng chưa được xác thực.");
        }
        return authentication.getName();
    }
}
