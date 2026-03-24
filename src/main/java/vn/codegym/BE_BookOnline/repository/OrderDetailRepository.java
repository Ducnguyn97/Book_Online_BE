package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;
import vn.codegym.BE_BookOnline.model.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Check existence of any order detail referencing a given book id
    boolean existsByBook_Id(Long bookId);

    boolean existsByBook_IdAndOrder_OrderStatusIn(Long bookId, List<OrderStatus> statuses);
}
