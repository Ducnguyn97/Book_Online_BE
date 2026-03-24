package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponse {

    // Thông tin người nhận
    private CheckoutAddressResponse shippingAddress;

    // Danh sách sản phẩm trong đơn
    private List<CheckoutItemResponse> items;
    private Integer totalItems;             // tổng số lượng

    // Tính tiền
    private BigDecimal subtotal;            // tổng tiền hàng
    private BigDecimal shippingFee;         // phí ship từ GHTK
    private BigDecimal discountAmount;      // giảm giá (coupon — hiện tại = 0)
    private BigDecimal totalAmount;         // = subtotal + shippingFee - discountAmount

    // Lựa chọn giao hàng — danh sách dịch vụ GHTK kèm phí tương ứng
    private List<DeliveryOptionResponse> deliveryOptions;

    // Lựa chọn thanh toán
    private List<PaymentOptionResponse> paymentMethods;

    // Thời gian giao hàng dự kiến từ GHTK
    private String estimatedDeliveryDate;

}