package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;
import vn.codegym.BE_BookOnline.model.Book;

import java.math.BigDecimal;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.typeBooks g
        WHERE (:keyword IS NULL OR LOWER(b.nameBook)   LIKE LOWER(CONCAT('%',:keyword,'%'))
                                OR LOWER(b.authorBook) LIKE LOWER(CONCAT('%',:keyword,'%')))
        AND (:genre    IS NULL OR LOWER(g.nameTypeBook)        = LOWER(:genre))
        AND (:author   IS NULL OR LOWER(b.authorBook)  = LOWER(:author))
        AND (:minPrice IS NULL OR b.priceBook          >= :minPrice)
        AND (:maxPrice IS NULL OR b.priceBook          <= :maxPrice)
        """,
            countQuery = """
        SELECT COUNT(DISTINCT b.id) FROM Book b
        LEFT JOIN b.typeBooks g
        WHERE (:keyword IS NULL OR LOWER(b.nameBook)   LIKE LOWER(CONCAT('%',:keyword,'%'))
                                OR LOWER(b.authorBook) LIKE LOWER(CONCAT('%',:keyword,'%')))
        AND (:genre    IS NULL OR LOWER(g.nameTypeBook)        = LOWER(:genre))
        AND (:author   IS NULL OR LOWER(b.authorBook)  = LOWER(:author))
        AND (:minPrice IS NULL OR b.priceBook          >= :minPrice)
        AND (:maxPrice IS NULL OR b.priceBook          <= :maxPrice)
        """)
    Page<Book> searchBooks(@Param("keyword") String keyword,
                           @Param("genre")   String genre,
                           @Param("author")  String author,
                           @Param("minPrice") BigDecimal minPrice,
                           @Param("maxPrice") BigDecimal maxPrice,
                           Pageable pageable);
}
