package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.request.AddToCartRequest;
import vn.codegym.BE_BookOnline.dto.response.BookDetailsResponse;
import vn.codegym.BE_BookOnline.dto.response.CartItemResponse;
import vn.codegym.BE_BookOnline.dto.response.CartResponse;
import vn.codegym.BE_BookOnline.model.*;
import vn.codegym.BE_BookOnline.repository.BookRepository;
import vn.codegym.BE_BookOnline.repository.CartItemRepository;
import vn.codegym.BE_BookOnline.repository.CartRepository;
import vn.codegym.BE_BookOnline.repository.UserRepository;
import vn.codegym.BE_BookOnline.service.CartService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j

public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Long countCartByUserEmail(String email) {

        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));
        Long totalItems = cartRepository.countTotalItemsInCart(cart.getId());
        return totalItems != null ? totalItems : 0L;
    }

    @Override
    @Transactional
    public void addToCart(String email, AddToCartRequest request) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sách với ID: " + request.getBookId()));
        //kiem tra sach da ton tai trong gio hang
        Optional<CartItem> existingCartItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();
        // neu da ton tai thi update quantity
        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingCartItem);
            // neu chua ton tai thi tao moi
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(request.getQuantity())
                    .price(book.getPriceBook())
                    .build();
            cart.getCartItems().add(newCartItem);
        }
        cart.setLastUpdated(java.time.LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserEmail(String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));

        List<CartItemResponse> cartItems = cart.getCartItems().stream()
                .map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice()) // Giá tại thời điểm thêm vào giỏ
                        .books(BookDetailsResponse.builder() // Đổi thành book (số ít) nếu DTO định nghĩa vậy
                                .id(item.getBook().getId())
                                .name(item.getBook().getNameBook())
                                .author(item.getBook().getAuthorBook())
                                .price(item.getBook().getPriceBook())
                                .image(item.getBook().getImageUrls())
                                .description(item.getBook().getDescriptionBook())
                                .publisher(item.getBook().getPublisherBook())
                                .genre(item.getBook().getTypeBooks().stream()
                                        .map(Genre::getNameTypeBook)
                                        .toList())
                                .quantity(item.getBook().getQuantityBook())
                                .soldQuantityBook(item.getBook().getSoldQuantityBook())
                                .rating(item.getBook().getAverageRating())
                                .build())
                        .build())
                .toList();
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer totalQuantity = cartItems.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();
        return CartResponse.builder()
                .items(cartItems)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .build();
    }

    @Override
    @Transactional
    public void clearCartByUserEmail(String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));
        cart.clearCart();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(String email, Long bookId) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));        //kiem tra sach da ton tai trong gio hang
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Sách không tồn tại trong giỏ hàng: " + bookId));
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);

    }

    @Override
    @Transactional
    public void updateCartItemQuantity(String email, Long bookId, Integer quantity) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Cart cart = cartRepository.findByUserId(user.getId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));
        //kiem tra sach da ton tai trong gio hang
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Sách không tồn tại trong giỏ hàng: " + bookId));
        if (quantity <= 0) {
            cart.getCartItems().remove(cartItem);
        }else{
            cartItem.setQuantity(quantity);
            cart.setLastUpdated(LocalDateTime.now());
            cartItemRepository.save(cartItem);
        }
        cartRepository.save(cart);
    }
}
