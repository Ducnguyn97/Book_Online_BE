package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.BookDetailsResponse;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;
import vn.codegym.BE_BookOnline.model.Book;
import vn.codegym.BE_BookOnline.model.Genre;
import vn.codegym.BE_BookOnline.model.Image;
import vn.codegym.BE_BookOnline.repository.BookRepository;
import vn.codegym.BE_BookOnline.service.BookService;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j

public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    @Override
    public BookResponse createBook(BookCreateRequest request, String email) {
        return null;
    }

    @Override
    public BookResponse updateBook(Long bookId, BookCreateRequest request, String email) {
        return null;
    }

    @Override
    public void deleteBook(Long bookId, String email) {

    }

    @Override
    public BookDetailsResponse getBookDetails(Long bookId) {
        return null;
    }

    @Override
    public Page<BookResponse> getAllBookByAuthor(String authorName, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public List<BookResponse> getAllBookByISBN(String isbn) {
        return List.of();
    }

    @Override
    public List<BookResponse> getNewBookOnShelves() {
        return List.of();
    }

    @Override
    public List<BookResponse> getBestsellingBooks() {
        return List.of();
    }

    @Override
    public Page<BookResponse> searchBooks(String keyword, String genre,
                                          String author, BigDecimal minPrice,
                                          BigDecimal maxPrice,
                                          int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá tối thiểu không được lớn hơn giá tối đa");
        }
        String cleanKeyword = (keyword != null && !keyword.isBlank())
                ? keyword.trim() : null;
        String cleanGenre   = (genre   != null && !genre.isBlank())
                ? genre.trim()   : null;
        String cleanAuthor  = (author  != null && !author.isBlank())
                ? author.trim()  : null;

        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<Book> books = bookRepository.searchBooks(cleanKeyword, cleanGenre, cleanAuthor, minPrice, maxPrice, pageable);
        return books.map(this::mapToBookResponse);
    }

    @Override
    public List<BookResponse> getRelatedBooks(Long bookId) {
        return List.of();
    }

    @Override
    public List<String> getAllGenres() {
        return List.of();
    }

    private BookResponse mapToBookResponse(Book book) {
        List<String> imageUrls = book.getImageBook() == null
                ? List.of()
                : book.getImageBook().stream()
                .map(Image::getUrlImage)
                .toList();
        List<String> genres = book.getTypeBooks() == null
                ? List.of()
                : book.getTypeBooks().stream()
                .map(Genre::getNameTypeBook)
                .toList();
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getNameBook())
                .author(book.getAuthorBook())
                .description(book.getDescriptionBook())
                .price(book.getPriceBook())
                .imagesUrls(imageUrls)
                .genres(genres)
                .build();
    }
}
