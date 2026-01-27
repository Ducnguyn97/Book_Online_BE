package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
