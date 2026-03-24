package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Vui lòng chọn địa chỉ giao hàng")
    private Long addressId;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private Long paymentId;

    @NotNull(message = "Vui lòng chọn phương thức giao hàng")
    private Long deliveryId;

    // Phí ship đã được tính từ bước getCheckoutInfo — gửi lên để server verify lại
    // Tránh trường hợp client tự ý thay đổi phí
    @NotNull(message = "Thiếu thông tin phí vận chuyển")
    private BigDecimal shippingFee;

    private String note;

    // Coupon — bỏ qua trong lần này, sẽ implement sau
    // private String couponCode;

    // ------------------------------------------------------------------
    // Luồng "Mua ngay" (Buy Now) — đặt 1 sản phẩm không qua giỏ hàng
    // Nếu buyNow = true thì bắt buộc có bookId + quantity
    // Nếu buyNow = false (mặc định) → server lấy toàn bộ cart của user
    // ------------------------------------------------------------------
    @Builder.Default
    private Boolean buyNow = false;

    // Chỉ dùng khi buyNow = true
    private Long bookId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}