package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
