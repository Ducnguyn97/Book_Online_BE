package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Feedbacks;

@Repository
public interface FeedbackRepository  extends JpaRepository<Feedbacks, Integer> {
}
