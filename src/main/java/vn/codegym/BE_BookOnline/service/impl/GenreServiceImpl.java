package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.model.Genre;
import vn.codegym.BE_BookOnline.service.GenreService;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {
    @Override
    public List<Genre> findAllGenres() {
        return List.of();
    }

    @Override
    public List<Genre> findGenresWithBook() {
        return List.of();
    }

    @Override
    public GenreResponse createGenre(String email, GenreCreateRequest request) {
        return null;
    }

    @Override
    public GenreResponse updateGenre(String email, Long genreId, GenreCreateRequest request) {
        return null;
    }
}
