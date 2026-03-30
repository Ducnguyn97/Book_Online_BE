package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.codegym.BE_BookOnline.dto.response.BookResponse;
import vn.codegym.BE_BookOnline.service.StaffService;
@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {
    @Override
    public Page<BookResponse> getAllBooksForStaff(int page, int size) {
        return null;
    }
}
