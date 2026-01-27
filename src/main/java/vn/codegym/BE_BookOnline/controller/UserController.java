package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.UserLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UserRegisterRequest;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
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
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String token){
        userService.verifyAccount(token);
        return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công");
    }

}
