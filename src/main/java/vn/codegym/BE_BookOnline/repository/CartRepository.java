package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Cart;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @Query("""
                SELECT COALESCE(SUM(ci.quantity), 0)
                FROM CartItem ci
                WHERE ci.cart.id = :cartId
            """)
    Long countTotalItemsInCart(@Param("cartId") Long cartId);
}
