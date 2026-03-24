package vn.codegym.BE_BookOnline.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllGenres(){
        List<GenreResponse> genres = genreService.findAllGenres();
        return ResponseEntity.ok(genres);
    }
    @GetMapping("/with-books")
    public ResponseEntity<List<GenreResponse>> getAllGenresWithBooks(@RequestParam Long bookId){
        List<GenreResponse> genres = genreService.findGenresWithBook(bookId);
        return ResponseEntity.ok(genres);
    }
}
