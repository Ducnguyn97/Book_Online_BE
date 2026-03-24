package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByBook_Id(Long bookId);
    long countByBook_Id(Long bookId);
    void deleteAllByBook_Id(Long bookId);
}

