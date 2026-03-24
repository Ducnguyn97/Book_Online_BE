package vn.codegym.BE_BookOnline.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p FROM Payment p WHERE p.active = true
            """)
    List<Payment> findByActiveTrue();
}
