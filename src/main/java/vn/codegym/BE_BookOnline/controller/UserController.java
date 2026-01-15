package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import vn.codegym.BE_BookOnline.dto.request.UserLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UserRegisterRequest;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.service.UserService;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final View error;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request,
                                      BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errorMsg = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body("lỗi dữ liệu đầu vào: " + errorMsg );
        }
        try {
            userService.registerUser(request);
            return ResponseEntity.ok("Đăng ký tài khoản thành công! Vui lòng kiểm tra email. ");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Đăng ký thất bại do lỗi hệ thống. ");
        }

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request){
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
