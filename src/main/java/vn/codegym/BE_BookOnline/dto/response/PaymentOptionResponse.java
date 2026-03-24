package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOptionResponse {
    private Long paymentId;
    private String name;                // "VNPay", "MoMo", "COD"
    private BigDecimal fee;             // phí thanh toán (nếu có)
    private String description;
    private boolean active;
}
