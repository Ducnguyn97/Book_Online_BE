package vn.codegym.BE_BookOnline.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;
import vn.codegym.BE_BookOnline.model.Enum.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long idOrder;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;// Ngày đặt hàng

    @Column(name = "complete_at")
    private LocalDateTime completeAt;

    @Column(name = "cancel_at")
    private LocalDateTime cancelAt;
    //snapshot địa chỉ giao hàng lưu chuỗi đầy đủ tránh mất dữ liệu khi user xóa địa chỉ
    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;// Địa chỉ giao hàng

    @Column(name = "phone_number_customer", nullable = false)
    private String phoneNumberCustomer;// Số điện thoại liên hệ

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;// Tên người nhận

    @Column(name = "total_price_products", nullable = false)
    private BigDecimal totalPriceProducts;// Tổng tiền hàng chưa bao gồm phí ship và giảm giá

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;// Tổng tiền đơn hàng bao gồm hết cac loại phí và giảm giá

    @Column(name = "fee_delivery", nullable = false)
    private BigDecimal feeDelivery;// Phí vận chuyển

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;// Số tiền giảm giá

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;// Trạng thái đơn hàng

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "note")
    private String note;// Ghi chú đơn hàng

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();// Chi tiết đơn hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;// Người dùng đặt hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_delivery", nullable = false, foreignKey = @ForeignKey(name = "fk_order_delivery"))
    private Delivery delivery;// Phương thức giao hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_payment", nullable = false, foreignKey = @ForeignKey(name = "fk_order_payment"))
    private Payment payment;// Phương thức thanh toán

}
