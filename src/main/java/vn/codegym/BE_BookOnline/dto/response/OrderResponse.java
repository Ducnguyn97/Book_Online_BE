package vn.codegym.BE_BookOnline.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;
import vn.codegym.BE_BookOnline.model.Enum.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderResponse {
    private Long orderNumber;
    private OrderStatus status;
    private String paymentMethod;
    private PaymentStatus paymentStatus;

    private String address;
    private String customerPhone;
    private String customerName;

    private List<OrderItemResponse> items;
    private Integer totalItems;

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;

    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;


}
