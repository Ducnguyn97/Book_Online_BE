package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query(value = """ 
            SELECT g.id, g.nameTypeBook 
            FROM Genre g 
            LEFT JOIN g.books b 
            WHERE b.id = :BookId""", nativeQuery = true)
    List<Genre> findGenresWithBook(@Param("BookId") Long bookId);

    boolean existsByNameTypeBookIgnoreCase(String nameTypeBook);

    Optional<Genre> findByNameTypeBookIgnoreCase(String nameTypeBook);
}
