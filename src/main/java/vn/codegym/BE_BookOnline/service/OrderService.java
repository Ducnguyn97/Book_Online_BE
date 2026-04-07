package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.codegym.BE_BookOnline.dto.request.OrderRequest;
import vn.codegym.BE_BookOnline.dto.request.OrderStatusRequest;
import vn.codegym.BE_BookOnline.dto.response.CheckoutResponse;
import vn.codegym.BE_BookOnline.dto.response.OrderResponse;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service xử lý đơn hàng
 *
 * Flow chuẩn:
 *   1. getCheckoutInfo()        — lấy thông tin tóm tắt + tính phí ship cho tất cả delivery options
 *   2. calculateShippingFee()   — (optional) tính lại phí khi user đổi địa chỉ hoặc dịch vụ ship
 *   3. createOrder()            — tạo đơn hàng, verify lại phí ship từ server
 *   4. cancelOrder()            — hủy đơn (chỉ khi status cho phép)
 *   5. getUserOrders()          — lấy danh sách đơn của user
 *   6. getOrderDetails()        — lấy chi tiết 1 đơn
 *   7. updateOrderStatus()      — Admin cập nhật trạng thái
 */
public interface OrderService {

    // ------------------------------------------------------------------
    // Checkout
    // ------------------------------------------------------------------

    /**
     * Lấy thông tin tóm tắt trước khi đặt hàng:
     * - Danh sách sản phẩm trong cart
     * - Tất cả delivery options kèm phí ship (gọi GHTK cho từng loại)
     * - Danh sách payment methods
     *
     * @param email  email user đang đăng nhập
     * @param addressId  địa chỉ giao hàng được chọn
     */
    CheckoutResponse getCheckoutInfo(String email, Long addressId);

    /**
     * Tính lại phí ship khi user đổi địa chỉ hoặc chọn loại dịch vụ khác
     *
     * @param email       email user
     * @param addressId   địa chỉ giao hàng
     * @param deliveryId  loại dịch vụ giao hàng
     */
    BigDecimal calculateShippingFee(String email, Long addressId, Long deliveryId);

    // ------------------------------------------------------------------
    // Order management
    // ------------------------------------------------------------------

    /**
     * Tạo đơn hàng:
     * - Validate cart / buyNow
     * - Verify shippingFee từ request với GHTK (chênh lệch > 1000đ thì reject)
     * - Tạo Order + OrderDetail (snapshot tên, ảnh, giá)
     * - Trừ tồn kho
     * - Xóa cart (nếu không phải buyNow)
     */
    OrderResponse createOrder(String email, OrderRequest orderRequest);

    /**
     * Hủy đơn hàng — chỉ cho phép khi status là PENDING hoặc CONFIRMED
     *
     * @param reason  lý do hủy
     */
    OrderResponse cancelOrder(String email, Long orderId, String reason);

    /**
     * Lấy chi tiết 1 đơn hàng (user chỉ xem được đơn của mình)
     */
    OrderResponse getOrderDetails(String email, Long orderId);

    /**
     * Lấy danh sách tất cả đơn hàng của user, sắp xếp mới nhất lên đầu
     */
    Page<OrderResponse> getUserOrders(String email, Pageable pageable);

    /**
     * Admin cập nhật trạng thái đơn hàng
     *
     * @param status  tên status mới (VD: "CONFIRMED", "SHIPPING", "DELIVERED")
     */
    OrderResponse updateOrderStatus(Long orderId, OrderStatusRequest request);

}