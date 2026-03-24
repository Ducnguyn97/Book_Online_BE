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
public class DeliveryOptionResponse {
    private Long deliveryId;
    private String name;                // "Giao hàng tiết kiệm"
    private BigDecimal fee;             // phí ship tính từ GHTK cho địa chỉ này
    private String estimatedDate;       // "3-5 ngày"
    private String description;
}
