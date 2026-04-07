package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;
import vn.codegym.BE_BookOnline.model.Book;
import vn.codegym.BE_BookOnline.model.Genre;
import vn.codegym.BE_BookOnline.model.User;
import vn.codegym.BE_BookOnline.model.Enum.BookStatus;
import vn.codegym.BE_BookOnline.repository.BookRepository;
import vn.codegym.BE_BookOnline.repository.UserRepository;
import vn.codegym.BE_BookOnline.service.StaffService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional(readOnly = true)
    public Page<StaffBookResponse> getAllBooksForStaff(String email,String keyword, String status, int page, int size) {

        int safePage = Math.max(page,0);
        int safeSize = Math.min(Math.max(size,0),20);

        String cleanKeyword = (keyword !=null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;
        String cleanStatus = (status !=null && !status.isBlank()) ? status.trim():null;

        Pageable pageable = PageRequest.of(safePage,safeSize);
        Page<Book> books = bookRepository.getAllBookForStaff(cleanKeyword, cleanStatus,pageable);

        return books.map(this::mapToStaffBookResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffBookResponse getBookByIdForStaff(Long bookId) {
        Book book = bookRepository.findByIdWithGenres(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách với id: " + bookId));
        return mapToStaffBookResponse(book);
    }

    @Override
    @Transactional
    public StaffBookResponse updateBookStatus(Long bookId, boolean active) {
        Book book = bookRepository.findByIdWithGenres(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách với id: " + bookId));
        book.setStatusBook(active ? BookStatus.ACTIVE : BookStatus.INACTIVE);
        bookRepository.save(book);
        return mapToStaffBookResponse(book);
    }

    @Override
    public Page<StaffBookResponse> getAllBooksAdvanceForStaff( String email, String genre, String author, String publisher, String isbn, int page, int size) {
            int safePage = Math.max(page,0);
            int safeSize = Math.min(Math.max(size,0),20);

            String cleanGenre = (genre !=null && !genre.isBlank()) ? genre.trim().toLowerCase() : null;
            String cleanAuthor = (author !=null && !author.isBlank()) ? author.trim().toLowerCase() : null;
            
            String cleanPublisher = (publisher !=null && !publisher.isBlank()) ? publisher.trim().toLowerCase() : null;
            String cleanIsbn = (isbn !=null && !isbn.isBlank()) ? isbn.trim().toLowerCase() : null;
            
            Pageable pageable = PageRequest.of(safePage,safeSize);
            Page<Book> books = bookRepository.getAllBookAdvanceForStaff(cleanGenre, cleanAuthor, cleanPublisher, cleanIsbn,pageable);
            return books.map(this::mapToStaffBookResponse); 
    }

    private StaffBookResponse mapToStaffBookResponse(Book book) {
        List<String> imageUrls = book.getImageUrls() == null ? List.of() : book.getImageUrls();
        List<String> genres = book.getTypeBooks() == null
                ? List.of()
                : book.getTypeBooks().stream()
                .map(Genre::getNameTypeBook)
                .toList();

        return StaffBookResponse.builder()
                .id(book.getId())
                .name(book.getNameBook())
                .author(book.getAuthorBook())
                .description(book.getDescriptionBook())
                .price(book.getPriceBook())
                .genres(genres)
                .imagesUrls(imageUrls)
                .isbn(book.getIsbnBook())
                .quantity(book.getQuantityBook())
                .publisher(book.getPublisherBook())
                .discountBook(book.getDiscountBook())
                .averageRating(book.getAverageRating())
                .bookStatus(book.getStatusBook())
                .createdAt(book.getDateCreated())
                .createdBy(book.getCreatedBy())
                .build();

    }
}
