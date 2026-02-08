package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnlockUserRequest {

    @NotBlank(message = "Lý do mở khóa không được để trống.")
    private String reason;
}
