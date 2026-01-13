package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.codegym.BE_BookOnline.dto.response.WishlistResponse;


public interface WishlistService {

    void addToWishlist(String email, Long bookId);

    void removeFromWishlist(String email, Long bookId);

    boolean isBookInWishlist(String email, Long bookId);// kiểm tra nếu đã có trong wishlist rồi thì không thêm nữa

    Page<WishlistResponse> getWishlistByUserEmail(String email, Pageable pageable);

    Long countWishlistByUserEmail(String email);


}
