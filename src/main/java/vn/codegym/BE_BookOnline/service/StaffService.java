package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import vn.codegym.BE_BookOnline.dto.response.StaffBookResponse;

public interface StaffService {
    Page<StaffBookResponse> getAllBooksForStaff(String email, String keyword, String status, int page, int size);

    StaffBookResponse getBookByIdForStaff(String email, Long bookId);

    StaffBookResponse updateBookStatus(String email, Long bookId, boolean active);

    Page<StaffBookResponse> getAllBooksAdvanceForStaff(String email, String genre, String author, String publisher, String isbn, int page, int size);
}
