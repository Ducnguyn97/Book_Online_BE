package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.model.Genre;

import org.springframework.data.domain.Page;

import java.util.List;

public interface GenreService {
    Page<GenreResponse> findAllGenres(int page, int size);

    List<GenreResponse> findGenresWithBook(Long bookId);

    GenreResponse createGenre(String email, GenreCreateRequest request);

    GenreResponse updateGenre(String email, Long genreId, GenreCreateRequest request);

    void deleteGenresByStaffId(String email, Long genreId);
}
