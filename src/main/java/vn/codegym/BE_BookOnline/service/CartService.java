package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.AddToCartRequest;
import vn.codegym.BE_BookOnline.dto.response.CartResponse;

public interface CartService {

    Long countCartByUserEmail(String email);

    void addToCart(String email, AddToCartRequest request);

    CartResponse getCartByUserEmail(String email);

    void clearCartByUserEmail(String email);

    void removeFromCart(String email, Long bookId);

    void updateCartItemQuantity(String email, Long bookId, Integer quantity);



}
