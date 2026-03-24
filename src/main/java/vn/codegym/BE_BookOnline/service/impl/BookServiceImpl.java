package vn.codegym.BE_BookOnline.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.request.BookCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.BookDetailsResponse;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;
import vn.codegym.BE_BookOnline.model.Book;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;
import vn.codegym.BE_BookOnline.model.Genre;
import vn.codegym.BE_BookOnline.repository.BookRepository;
import vn.codegym.BE_BookOnline.repository.CartItemRepository;
import vn.codegym.BE_BookOnline.repository.GenreRepository;
import vn.codegym.BE_BookOnline.repository.OrderDetailRepository;
import vn.codegym.BE_BookOnline.service.BookService;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j

public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    @Transactional
    @Override
    public StaffBookResponse createBook(BookCreateRequest request, String email) {
        if(bookRepository.existsByNameBookIgnoreCase(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên sách đã đã tồn tại: " + request.getName());
        }
        // Xử lý genres: lấy Genre entities từ IDs
        List<Genre> genres = request.getGenreIds().stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy thể loại với ID: " + genreId)))
                .toList();

        // Tạo Book entity
        Book newBook = Book.builder()
                .nameBook(request.getName())
                .authorBook(request.getAuthor())
                .descriptionBook(request.getDescription())
                .priceBook(request.getPrice())
                .quantityBook(request.getQuantity())
                .publisherBook(request.getPublisher())
                .isbnBook(request.getIsbn())
                .imageUrls(request.getImageUrls())  // Trực tiếp gán List<String>
                .typeBooks(genres)
                .averageRating(0.0)
                .soldQuantityBook(0)
                .discountBook(0.0)
                .discountPercentBook(0.0)
                .isDeleted(false)
                .build();

        // Save và return response
        Book savedBook = bookRepository.save(newBook);

        return StaffBookResponse.builder()
                .id(savedBook.getId())
                .name(savedBook.getNameBook())
                .author(savedBook.getAuthorBook())
                .isbn(savedBook.getIsbnBook())
                .price(savedBook.getPriceBook())
                .quantity(savedBook.getQuantityBook())
                .publisher(savedBook.getPublisherBook())
                .imagesUrls(savedBook.getImageUrls())
                .genres(genres.stream().map(Genre::getNameTypeBook).toList())
                .description(savedBook.getDescriptionBook())
                .build();
    }

    @Transactional
    @Override
    public StaffBookResponse updateBook(Long bookId, BookCreateRequest request, String email) {
        Book bookToUpdate = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sách với ID: " + bookId));
        List<Genre> genres = request.getGenreIds().stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy thể loại với ID: " + genreId)))
                .toList();
        String newName = request.getName().trim();

        if(bookRepository.existsByNameBookIgnoreCase(newName)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên sách đã đã tồn tại: " + request.getName());
        }

        bookToUpdate.setNameBook(newName);
        bookToUpdate.setAuthorBook(request.getAuthor());
        bookToUpdate.setDescriptionBook(request.getDescription());
        bookToUpdate.setPriceBook(request.getPrice());
        bookToUpdate.setQuantityBook(request.getQuantity());
        bookToUpdate.setPublisherBook(request.getPublisher());
        bookToUpdate.setIsbnBook(request.getIsbn());
        bookToUpdate.setImageUrls(request.getImageUrls());
        bookToUpdate.setTypeBooks(genres);
        bookRepository.save(bookToUpdate);

        return null;
    }

    @Transactional
    @Override
    public void deleteBook(Long bookId, String email) {

        // 1. Kiểm tra sách tồn tại
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sách với ID: " + bookId));

        // 2. Chặn nếu sách đang có trong đơn hàng PENDING hoặc PROCESSING
        boolean hasActiveOrder = orderDetailRepository
                .existsByBook_IdAndOrder_OrderStatusIn(
                        bookId,
                        List.of(OrderStatus.PENDING, OrderStatus.PROCESSING));
        if (hasActiveOrder) {
            log.warn("Staff {} cố xóa sách id={} nhưng sách đang có trong đơn hàng chưa hoàn thành", email, bookId);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể xóa sách vì đang có trong đơn hàng chưa hoàn thành (PENDING/PROCESSING)");
        }

        // 3. Xóa khỏi giỏ hàng của tất cả khách hàng
        long cartCount = cartItemRepository.countByBook_Id(bookId);
        if (cartCount > 0) {
            cartItemRepository.deleteAllByBook_Id(bookId);
            log.info("Đã xóa {} cart item chứa sách id={}", cartCount, bookId);
        }

        // 4. Soft delete — @SQLDelete tự chạy: UPDATE books SET is_deleted = true
        bookRepository.delete(book);
        log.info("Staff {} đã soft-delete sách id={}", email, bookId);
    }
    @Override
    public BookDetailsResponse getBookDetails(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new
                        ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách với ID: " + bookId));
        bookRepository.save(book);
        List<String> imageUrls = book.getImageUrls() == null ? List.of() : book.getImageUrls();
        List<String> genres = book.getTypeBooks() == null
                ? List.of()
                : book.getTypeBooks().stream()
                .map(Genre::getNameTypeBook)
                .toList();
        return BookDetailsResponse.builder()
                .id(book.getId())
                .name(book.getNameBook())
                .author(book.getAuthorBook())
                .description(book.getDescriptionBook())
                .price(book.getPriceBook())
                .image(imageUrls)
                .genre(genres)
                .soldQuantityBook(book.getSoldQuantityBook())
                .quantity(book.getQuantityBook())
                .rating(book.getAverageRating())
                .publisher(book.getPublisherBook())
                .build();
    }

    @Override
    public Page<BookResponse> getAllBookByAuthor(String authorName, Pageable pageable) {
        Page<Book> bookByAuthor = bookRepository.findAllBookByAuthorBook(authorName, pageable);
        return bookByAuthor.map(this::mapToBookResponse);
    }

    @Override
    public Page<BookResponse> getAllBookByISBN(String isbn, Pageable pageable) {
        Page<Book> bookByISBN = bookRepository.findAllBookByIsbnBook(isbn, pageable);
        return bookByISBN.map(this::mapToBookResponse);
    }

    @Override
    public List<BookResponse> getNewBookOnShelves() {
        List<Book> newBookOnShelves = bookRepository.findNewBooksOnShelves(10);
        return newBookOnShelves.stream()
                .map(this::mapToBookResponse)
                .toList();
    }

    @Override
    public List<BookResponse> getBestsellingBooks() {
        List<Book> bestsellingBooks = bookRepository.findTop10BestSellingBooks();
        return bestsellingBooks.stream()
                .map(this::mapToBookResponse)
                .toList();
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
        Book book = bookRepository.findByIdWithGenres(bookId)
                .orElseThrow(() -> new
                        ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách với ID: " + bookId));
        List<Long> genreIds = book.getTypeBooks().stream()
                .map(Genre::getId)
                .distinct()
                .toList();

        if (genreIds.isEmpty()) {
            return List.of();
        }
        List<Book> relatedBooks = bookRepository.findRelatedBooksByGenreIds(
                bookId,
                genreIds,
                PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "soldQuantityBook"))
        );
        return relatedBooks.stream()
                .map(this::mapToBookResponse)
                .toList();
    }


    private BookResponse mapToBookResponse(Book book) {
        List<String> imageUrls = book.getImageUrls() == null ? List.of() : book.getImageUrls();
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
