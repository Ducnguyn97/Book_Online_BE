package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    @NotBlank(message = "Email không được bỏ trống.")
    @Email(message = "Email Không hợp lệ")
    private  String email;

    @NotBlank(message = "Mật kẩu không đúng.")
    private String password;
}
