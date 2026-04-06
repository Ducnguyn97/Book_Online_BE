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
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.dto.response.OrderResponse;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateStockQuantityResponse;
import vn.codegym.BE_BookOnline.service.BookService;
import vn.codegym.BE_BookOnline.service.GenreService;
import vn.codegym.BE_BookOnline.service.OrderService;
import vn.codegym.BE_BookOnline.service.StaffService;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final BookService bookService;
    private final GenreService genreService;
    private final StaffService staffService;
    private final OrderService orderService;

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
    //lay danh sach co filter
    @GetMapping("/books")
    public ResponseEntity<Page<StaffBookResponse>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) String status,
                                                               Authentication authentication) {
        String email = authentication.getName();
        Page<StaffBookResponse> books = staffService.getAllBooksForStaff(email, keyword, status,page, size);
        return ResponseEntity.ok(books);
    }
    //lay thong tin sach theo id
    @GetMapping("/books/{bookId}")
    public ResponseEntity<StaffBookResponse> getBookById(@PathVariable Long bookId) {
        StaffBookResponse book = staffService.getBookByIdForStaff(bookId);
        return ResponseEntity.ok(book);
    }
    //cap nhat trang thai sach
    @PatchMapping("/books/{bookId}/status")
    public ResponseEntity<StaffBookResponse> updateBookStatus(Authentication authentication,
                                                              @PathVariable Long bookId,
                                                              @RequestParam boolean active) {
        String email = authentication.getName();
        StaffBookResponse updatedBook = staffService.updateBookStatus(email, bookId, active);
        return ResponseEntity.ok(updatedBook);
    }
    //tim kiem sach nang cao
    @GetMapping("/books/search")
    public ResponseEntity<Page<StaffBookResponse>> searchAdvancedBooks(Authentication authentication,
                                                                      @RequestParam(required = false) String genre,
                                                                      @RequestParam(required = false) String author,
                                                                      @RequestParam(required = false) String publisher,
                                                                      @RequestParam(required = false) String isbn,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        Page<StaffBookResponse> books = staffService.getAllBooksAdvanceForStaff(email, genre, author, publisher,isbn,page,size);
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/genres/{genresId}")
    public ResponseEntity<?> deleteGenre(Authentication authentication, @PathVariable("genresId") Long genresId) {
        String email = authentication.getName();
        genreService.deleteGenresByStaffId(email,genresId);
        return ResponseEntity.ok("Xoá Genres thành công.");
    }

    @PatchMapping("/books/{bookId}/quantity")
    public ResponseEntity<UpdateStockQuantityResponse> updateStockQuantity(Authentication authentication,
                                                                           @PathVariable("bookId") Long bookId,
                                                                           @RequestParam int quantity){
        String email = authentication.getName();
        UpdateStockQuantityResponse updateStockQuantityResponse = bookService.updateStockQuantity(email, bookId, quantity);
        return ResponseEntity.ok(updateStockQuantityResponse);
    }
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatusByStaff(Authentication authentication,
                                                                  @PathVariable("orderId") Long orderId,
                                                                  @RequestParam String status){
        String email = authentication.getName();
        OrderResponse orderResponse = orderService.UpdateOrderStatusByStaff(email, orderId, status);
        return ResponseEntity.ok(orderResponse);
    }



}
