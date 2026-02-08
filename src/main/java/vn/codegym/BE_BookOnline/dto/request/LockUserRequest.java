package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LockUserRequest {
    @NotBlank(message = "Lý do khóa không được để trống")
    private String reason;

}
