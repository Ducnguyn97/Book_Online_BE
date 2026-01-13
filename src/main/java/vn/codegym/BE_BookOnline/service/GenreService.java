package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.model.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAllGenres();

    List<Genre> findGenresWithBook();
}
