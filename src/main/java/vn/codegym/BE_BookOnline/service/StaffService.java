package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;

public interface StaffService {
    Page<BookResponse> getAllBooksForStaff(int page, int size);
}
