package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.*;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
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
    @PutMapping("/{userId}/lock")
    public ResponseEntity<UserProfile> lockUserAccount(@PathVariable Long userId, @Valid @RequestBody LockUserRequest request) {
        UserProfile response = userService.lockUserAccount(userId, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{userId}/unlock")
    public ResponseEntity<UserProfile> unlockUserAccount(@PathVariable Long userId, @Valid @RequestBody UnlockUserRequest request) {
        UserProfile response = userService.unlockUserAccount(userId, request);
        return ResponseEntity.ok(response);
    }

     @PutMapping("/change-password")
     public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
         String email = getEmailFromAuthContext();
         userService.changeUserPassword(email, request.getNewPassword(), request.getOldPassword());
         return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công!");
     }

     @PatchMapping("/change-avatar")
     public ResponseEntity<?> changeAvatar(@Valid @RequestBody ChangeAvatarRequest request) {
         String email = getEmailFromAuthContext();
         userService.changeUserAvatar(email, request.getAvatarUrl());
            return ResponseEntity.ok("Ảnh đại diện đã được cập nhật thành công!");
        }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.initiateForgotPassword(request.getEmail());
        return ResponseEntity.ok("Nếu email tồn tại, một liên kết đặt lại mật khẩu đã được gửi.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> completeForgotUserPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                        @RequestParam String token) {
        userService.completeForgotUserPassword(request.getNewPassword(), token);
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
    }

    private String getEmailFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Người dùng chưa được xác thực.");
        }
        return authentication.getName();
    }
}
