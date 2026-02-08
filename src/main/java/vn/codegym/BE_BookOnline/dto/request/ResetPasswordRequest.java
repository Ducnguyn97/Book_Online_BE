package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Mật khẩu mới không được để trống.")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự.")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
    @Size(min = 6, message = "Xác nhận mật khẩu phải có ít nhất 6 ký tự.")
    private String confirmPassword;

}
