package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Delivery;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("""
            SELECT d FROM Delivery d WHERE d.active = true
            """)
    List<Delivery> findByActiveTrue();
}
