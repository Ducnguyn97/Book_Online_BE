package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import vn.codegym.BE_BookOnline.model.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
            """, countQuery = """
            SELECT COUNT(DISTINCT b.id) FROM Book b
            LEFT JOIN b.typeBooks g
            WHERE (:keyword IS NULL OR LOWER(b.nameBook)   LIKE LOWER(CONCAT('%',:keyword,'%'))
                                    OR LOWER(b.authorBook) LIKE LOWER(CONCAT('%',:keyword,'%')))
            AND (:genre    IS NULL OR LOWER(g.nameTypeBook)        = LOWER(:genre))
            AND (:author   IS NULL OR LOWER(b.authorBook)  = LOWER(:author))
            AND (:minPrice IS NULL OR b.priceBook          >= :minPrice)
            AND (:maxPrice IS NULL OR b.priceBook          <= :maxPrice)
            """)
    Page<Book> searchBooks(@Param("keyword") String keyword, @Param("genre") String genre, @Param("author") String author, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("""
            select distinct b
            from Book b
            join b.typeBooks g
            where g.id in :genreIds
              and b.id <> :bookId
              and b.quantityBook > 0
            """)
    List<Book> findRelatedBooksByGenreIds(@Param("bookId") Long bookId, @Param("genreIds") List<Long> genreIds, Pageable pageable);

    @Query("""
            select b from Book b
            left join fetch b.typeBooks
            where b.id = :bookId
            """)
    Optional<Book> findByIdWithGenres(@Param("bookId") Long bookId);

    @Query(value = "SELECT * FROM books WHERE is_deleted = false ORDER BY sold_quantity_book DESC LIMIT 10", nativeQuery = true)
    List<Book> findTop10BestSellingBooks();

    @Query(value = """
            SELECT b.id_book, b.name_book, b.author_book, b.price_book, 
                   b.discount_percent_book, b.average_rating, b.sold_quantity_book
            FROM books b
            WHERE b.is_deleted = false 
              AND b.date_created IS NOT NULL
              AND b.quantity_book > 0
            ORDER BY b.date_created DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Book> findNewBooksOnShelves(@Param("limit") int limit);

    //like concat('%',:keyword,'%') lay tat ca cac ky tu co trong keyword
    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(b.authorBook) LIKE LOWER(CONCAT('%', :authorName, '%'))
              AND b.quantityBook > 0
            ORDER BY b.soldQuantityBook DESC
            """)
    Page<Book> findAllBookByAuthorBook(@Param("authorName") String authorName, Pageable pageable);

    @Query("""
                SELECT b FROM Book b
                WHERE LOWER(b.isbnBook) LIKE LOWER(CONCAT('%', :isbn, '%'))
                  AND b.quantityBook > 0
                ORDER BY b.soldQuantityBook DESC
            """)
    Page<Book> findAllBookByIsbnBook(String isbn, Pageable pageable);

    boolean existsByNameBookIgnoreCase(String nameBook);

    @Query(value = """
            select distinct b from Book b
                        left join fetch b.typeBooks g
            where (:keyword is null or lower(b.nameBook) like lower(concat('%', :keyword, '%'))
                                    or lower(b.authorBook) like lower(concat('%', :keyword, '%'))
                                    or lower(g.nameTypeBook) like lower(concat('%', :keyword, '%')))
                                    and (:status is null or b.statusBook = :status)
            """, countQuery = """
            select count(distinct b.id) from Book b
            left join b.typeBooks g
            where (:keyword is null or lower(b.nameBook) like lower(concat('%', :keyword, '%'))
                                    or lower(b.authorBook) like lower(concat('%', :keyword, '%'))
                                    or lower(g.nameTypeBook) like lower(concat('%', :keyword, '%')))
                                    and (:status is null or b.statusBook = :status)

            """)
    Page<Book> getAllBookForStaff(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    @Query(value ="""
    select distinct b from Book b
    left join fetch b.typeBooks g
    where (:genre is null or lower(g.nameTypeBook) like lower(:genre))
    and (:author is null or lower(b.authorBook) like lower(:author))
    and (:publisher is null or lower(b.publisherBook) like lower(:publisher))
    and (:isbn is null or lower(b.isbnBook) like lower(:isbn))
    """, countQuery = """
    select count(distinct b.id) from Book b
        left join b.typeBooks g
        where (:genre is null or lower(g.nameTypeBook) like lower(:genre))
        and (:author is null or lower(b.authorBook) like lower(:author))
        and (:publisher is null or lower(b.publisherBook) like lower(:publisher))
        and (:isbn is null or lower(b.isbnBook) like lower(:isbn))
        """)
        Page<Book> getAllBookAdvanceForStaff(@Param("genre") String genre,
         @Param("author") String author, 
         @Param("publisher") String publisher,
         @Param("isbn") String isbn, 
         Pageable pageable);   
         
     @Query(value = """
                     select b from Book b
                     where b.id = :bookId
                     and b.is_deleted = false
                     """, nativeQuery = true)
        Optional<Book> findByIdAndNotDeleted(@Param("bookId") Long bookId);
}
