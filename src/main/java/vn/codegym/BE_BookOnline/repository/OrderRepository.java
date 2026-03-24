package vn.codegym.BE_BookOnline.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(Long userId);/**
     * Lấy danh sách đơn hàng của user — fetch join OrderDetail để tránh N+1 query
     * Không fetch join Book vì mapToOrderResponse dùng snapshot (productName, thumbnailAtPurchase)
     * không cần load cả Book entity
     */
    @Query("""
            SELECT DISTINCT o FROM Order o
            LEFT JOIN FETCH o.orderDetails od
            WHERE o.user.id = :userId
            ORDER BY o.orderDate DESC
            """)
    Page<Order> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    /**
     * Lấy chi tiết 1 đơn hàng — verify ownership (user chỉ xem được đơn của mình)
     */
    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderDetails od
            WHERE o.idOrder = :orderId AND o.user.id = :userId
""")
    Optional<Order> findByIdAndUserId(@Param("orderId") Long orderId,
                                      @Param("userId") Long id);
    /**
     * Dùng cho Admin — lấy đơn theo id không cần verify user
     */
    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderDetails od
            WHERE o.idOrder = :orderId
""")
    Optional<Order> findByIdWithDetails(@Param("orderId")Long orderId);
}
