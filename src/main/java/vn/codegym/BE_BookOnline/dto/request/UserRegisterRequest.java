package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.codegym.BE_BookOnline.model.Role;

import java.util.List;

@Data
public class UserRegisterRequest {
    private String username;

    @NotBlank(message = "Email không được bỏ trống.")
    @Email(message = "Email không hợp lệ.")
    private String email;

    @NotBlank(message = "Mật khẩu không được đêt trống.")
    @Size(min = 6, message = "Mật kẩu phải có it nhất 6 ký tự. ")
    private String password;

    @NotBlank(message = "Mật khẩu không được để trống.")
    private String confirmPassword;

    private List<Role> roles;
}
