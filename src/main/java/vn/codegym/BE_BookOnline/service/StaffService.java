package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import vn.codegym.BE_BookOnline.dto.response.BookResponseStaff;

public interface StaffService {
    Page<BookResponseStaff> getAllBooksForStaff(int page, int size);

    BookResponseStaff getBookByIdForStaff(Long bookId);

    BookResponseStaff updateBookStatus(String email, Long bookId, boolean active);
}
