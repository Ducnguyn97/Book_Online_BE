package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.BookDetailsResponse;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;
import vn.codegym.BE_BookOnline.service.BookService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBook(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String genre,
                                                         @RequestParam(required = false) String authorName,
                                                         @RequestParam(required = false) @DecimalMin(value = "0", message = "Giá tối thiểu không được âm") BigDecimal minPrice,
                                                         @RequestParam(required = false) @DecimalMin(value = "0", message = "Giá tối đa không được âm") BigDecimal maxPrice,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {

        Page<BookResponse> books = bookService.searchBooks(keyword, genre, authorName, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsResponse> getBookDetails(@PathVariable Long bookId) {
       BookDetailsResponse response = bookService.getBookDetails(bookId);
       return ResponseEntity.ok(response);
    }
    @GetMapping("/{BookId}/related")
    public ResponseEntity<List<BookResponse>> getBookRelated(@PathVariable Long BookId) {
        List<BookResponse> relatedBooks = bookService.getRelatedBooks(BookId);
        return ResponseEntity.ok(relatedBooks);
    }
    @GetMapping("/top-selling")
    public ResponseEntity<List<BookResponse>> getTopSellingBooks() {
        List<BookResponse> topSellingBooks = bookService.getBestsellingBooks();
        return ResponseEntity.ok(topSellingBooks);
    }
    @GetMapping("/new-books")
    public ResponseEntity<List<BookResponse>> getNewBooksOnShelves() {
        List<BookResponse> newBooksOnShelves = bookService.getNewBookOnShelves();
        return ResponseEntity.ok(newBooksOnShelves);
    }
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getAllGenres() {
        List<String> genres = bookService.getAllGenres();
        return ResponseEntity.ok(genres);
    }
    @GetMapping("/authors")
    public ResponseEntity<Page<BookResponse>> getAllBookByAuthor(@RequestParam String authorName,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> booksByAuthor = bookService.getAllBookByAuthor(authorName,pageable);
        return ResponseEntity.ok(booksByAuthor);
    }
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<BookResponse>> getAllBookByISBN(@PathVariable String isbn) {
        List<BookResponse> booksByISBN = bookService.getAllBookByISBN(isbn);
        return ResponseEntity.ok(booksByISBN);
    }


}
