package vn.codegym.BE_BookOnline.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
