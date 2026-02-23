package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.model.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAllGenres();

    List<Genre> findGenresWithBook();

    GenreResponse createGenre(String email, GenreCreateRequest request);

    GenreResponse updateGenre(String email, Long genreId, GenreCreateRequest request);
}
