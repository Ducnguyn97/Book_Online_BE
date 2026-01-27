package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.codegym.BE_BookOnline.dto.request.BookRequest;
import vn.codegym.BE_BookOnline.dto.response.BookDetailsResponse;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {
    BookResponse createBook(BookRequest request, String email);

    BookResponse updateBook(Long bookId, BookRequest request, String email);

    void deleteBook(Long bookId, String email);

    BookDetailsResponse getBookDetails(Long bookId);

    List<BookResponse> getAllBookByAuthor(String authorName);

    List<BookResponse> getAllBookByISBN(String isbn);

    List<BookResponse> getTopSuggestedBooks();

    List<BookResponse> getTop8MostDiscountedBooks();

    Page<BookResponse> searchBooks(Long BookId, String genre, String author, BigDecimal minPice, BigDecimal maxPrice, String title, Pageable pageable);
}
