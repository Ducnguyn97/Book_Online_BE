package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;
import vn.codegym.BE_BookOnline.exception.UnauthorizedException;
import vn.codegym.BE_BookOnline.service.BookService;
import vn.codegym.BE_BookOnline.service.GenreService;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private  BookService bookService;
    private final GenreService genreService;

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        String email = getEmailFromAuthContext();
        bookService.deleteBook(bookId, email);
        return ResponseEntity.ok("Xóa sách thành công");
    }
    @PostMapping("/books/create")
    public ResponseEntity<StaffBookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        String email = getEmailFromAuthContext();
        StaffBookResponse response= bookService.createBook(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/books/{bookId}")
    public ResponseEntity<StaffBookResponse> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookCreateRequest request) {
        String email = getEmailFromAuthContext();
        StaffBookResponse response = bookService.updateBook(bookId, request, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/genres")
    public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody GenreCreateRequest request) {
        String email = getEmailFromAuthContext();
        GenreResponse response = genreService.createGenre(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/genres/{genreId}")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable Long genreId,
            @Valid @RequestBody GenreCreateRequest request) {
        String email = getEmailFromAuthContext();
        GenreResponse response = genreService.updateGenre(email, genreId, request);
        return ResponseEntity.ok(response);
    }
    private String getEmailFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new UnauthorizedException("Không thể xác thực người dùng");
    }

}
