package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;
import vn.codegym.BE_BookOnline.service.BookService;
import vn.codegym.BE_BookOnline.service.GenreService;
import vn.codegym.BE_BookOnline.service.StaffService;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private  final BookService bookService;
    private final GenreService genreService;
    private final StaffService staffService;

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(Authentication authentication,@PathVariable Long bookId) {
        String email = authentication.getName();
        bookService.deleteBook(bookId, email);
        return ResponseEntity.ok("Xóa sách thành công");
    }
    @PostMapping("/books")
    public ResponseEntity<StaffBookResponse> createBook(Authentication authentication,@Valid @RequestBody BookCreateRequest request) {
        String email = authentication.getName();
        StaffBookResponse response= bookService.createBook(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/books/{bookId}")
    public ResponseEntity<StaffBookResponse> updateBook(Authentication authentication, @PathVariable Long bookId, @Valid @RequestBody BookCreateRequest request) {
        String email = authentication.getName();
        StaffBookResponse response = bookService.updateBook(bookId, request, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/genres")
    public ResponseEntity<GenreResponse> createGenre(Authentication authentication, @Valid @RequestBody GenreCreateRequest request) {
        String email = authentication.getName();
        GenreResponse response = genreService.createGenre(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/genres/{genreId}")
    public ResponseEntity<GenreResponse> updateGenre(
            Authentication authentication,
            @PathVariable Long genreId,
            @Valid @RequestBody GenreCreateRequest request) {
        String email = authentication.getName();
        GenreResponse response = genreService.updateGenre(email, genreId, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/books")
    public ResponseEntity<Page<BookResponse>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<BookResponse> books = staffService.getAllBooksForStaff(page, size);
        return ResponseEntity.ok(books);
    }

}
