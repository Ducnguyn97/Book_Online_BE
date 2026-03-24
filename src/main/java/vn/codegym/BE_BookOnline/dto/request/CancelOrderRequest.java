package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelOrderRequest {
    @NotNull(message = "Lý do hủy đơn không được để trống")
    @Size(min = 150, message = "lý do không quá 200 ký tự")
    private String reason;
}
