package vn.codegym.BE_BookOnline.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long idOrder;

    @Column(name = "order_date", nullable = false)
    private Date orderDate;// Ngày đặt hàng

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;// Địa chỉ giao hàng

    @Column(name = "phone_number_customer", nullable = false)
    private String phoneNumberCustomer;// Số điện thoại liên hệ

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;// Tên người nhận

    @Column(name = "total_price_products", nullable = false)
    private BigDecimal totalPriceProducts;// Tổng tiền hàng

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;// Tổng tiền đơn hàng

    @Column(name = "fee_delivery", nullable = false)
    private double feeDelivery;// Phí vận chuyển

    @Column(name = "order_status", nullable = false)
    private String orderStatus;// Trạng thái đơn hàng

    @Column(name = "note")
    private String note;// Ghi chú đơn hàng

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;// Chi tiết đơn hàng

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;// Người dùng đặt hàng

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_delivery", nullable = false, foreignKey = @ForeignKey(name = "fk_order_delivery"))
    private Delivery delivery;// Phương thức giao hàng

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_payment", nullable = false, foreignKey = @ForeignKey(name = "fk_order_payment"))
    private Payment payment;// Phương thức thanh toán

}
