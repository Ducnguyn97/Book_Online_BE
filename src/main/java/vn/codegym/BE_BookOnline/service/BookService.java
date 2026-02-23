package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.BookDetailsResponse;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {
    BookResponse createBook(BookCreateRequest request, String email);

    BookResponse updateBook(Long bookId, BookCreateRequest request, String email);

    void deleteBook(Long bookId, String email);

    BookDetailsResponse getBookDetails(Long bookId);

    Page<BookResponse> getAllBookByAuthor(String authorName, Pageable pageable);

    List<BookResponse> getAllBookByISBN(String isbn);

    List<BookResponse> getNewBookOnShelves();

    List<BookResponse> getBestsellingBooks();

    // Đổi signature: nhận page, size thay vì Pageable
    Page<BookResponse> searchBooks(String keyword, String genre, String author,
                                   BigDecimal minPrice, BigDecimal maxPrice,
                                   int page, int size);

    List<BookResponse> getRelatedBooks(Long bookId);

    List<String> getAllGenres();
}
