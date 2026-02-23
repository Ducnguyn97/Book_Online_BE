package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;
import vn.codegym.BE_BookOnline.service.BookService;
import vn.codegym.BE_BookOnline.service.GenreService;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final BookService bookService;
    private final GenreService genreService;

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        String email = getEmailFromAuthContext();
        bookService.deleteBook(bookId, email);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/books/create")
    public ResponseEntity<StaffBookResponse> createBooks(@Valid @RequestBody BookCreateRequest request) {
        String email = getEmailFromAuthContext();
        bookService.createBook(request, email);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/books/{bookId}")
    public ResponseEntity<StaffBookResponse> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookCreateRequest request) {
        String email = getEmailFromAuthContext();
        bookService.updateBook(bookId, request, email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/genres/create")
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreCreateRequest request) {
        String email = getEmailFromAuthContext();
        genreService.createGenre(email, request);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/genres/{genreId}")
    public ResponseEntity<?> updateGenre(@PathVariable Long genreId, @Valid @RequestBody GenreCreateRequest request) {
        String email = getEmailFromAuthContext();
        genreService.updateGenre(email, genreId, request);
        return ResponseEntity.ok().build();
    }
    private String getEmailFromAuthContext() {
        return ((org.springframework.security.core.userdetails.User)
                (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUsername();
    }

}
