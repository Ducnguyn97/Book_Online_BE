package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.OrderRequest;
import vn.codegym.BE_BookOnline.dto.response.CheckoutResponse;
import vn.codegym.BE_BookOnline.dto.response.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service xử lý đơn hàng
 * Chịu trách nhiệm:
 * - CRUD đơn hàng
 * - Tính toán giá (phí, shipping, discount)
 * - Validate giỏ hàng
 * - Cập nhật trạng thái
 * - Xử lý thanh toán
 */
public interface OrderService {
    //checkout method
    CheckoutResponse getCheckoutInfo(String Email);

    CheckoutResponse applyDiscountCode(String email, String discountCode);

    BigDecimal calculateShippingFee(String email, Long addressId);
    //order management methods
    OrderResponse createOrder(String email, OrderRequest orderRequest);

    OrderResponse cancelOrder(String email, Long orderId, String reason);

    OrderResponse getOrderDetails(String email, Long orderId);

    List<OrderResponse> getUserOrders(String email);

    OrderResponse updateOrderStatus(Long orderId, String status);

    OrderResponse getOrderById(String email, Long orderId);

}
