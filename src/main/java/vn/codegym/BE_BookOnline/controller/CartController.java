package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.AddToCartRequest;
import vn.codegym.BE_BookOnline.dto.request.UpdateCartItemRequest;
import vn.codegym.BE_BookOnline.dto.response.CartCountResponse;
import vn.codegym.BE_BookOnline.dto.response.CartResponse;
import vn.codegym.BE_BookOnline.service.CartService;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/count")
    public ResponseEntity<CartCountResponse> countCartByUserEmail() {
        String email = getEmailFromAuthContext();
        Long count = cartService.countCartByUserEmail(email);
        return ResponseEntity.ok(new CartCountResponse(count));
    }

    @GetMapping("/cart-info")
    public ResponseEntity<CartResponse> getCartInfo() {
        String email = getEmailFromAuthContext();
        CartResponse cart = cartService.getCartByUserEmail(email);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<CartResponse> addToCart(@RequestBody @Valid AddToCartRequest request) {
        String email = getEmailFromAuthContext();
        cartService.addToCart(email, request);
        return ResponseEntity.ok(cartService.getCartByUserEmail(email));
    }

    @PutMapping("/update-cart/{bookId}")
    public ResponseEntity<CartResponse> updateCart(@RequestBody UpdateCartItemRequest request
    , @PathVariable Long bookId) {
        String email = getEmailFromAuthContext();
        cartService.updateCartItemQuantity(email, bookId, request.getQuantity());
        return ResponseEntity.ok(cartService.getCartByUserEmail(email));
    }
    @DeleteMapping("/remove-from-cart/{bookId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long bookId) {
        String email = getEmailFromAuthContext();
        cartService.removeFromCart(email, bookId);
        return ResponseEntity.ok(cartService.getCartByUserEmail(email));
    }
    @DeleteMapping("/clear-cart")
    public ResponseEntity<Void> clearCart() {
        String email = getEmailFromAuthContext();
        cartService.clearCartByUserEmail(email);
        return ResponseEntity.noContent().build();
    }

    private String getEmailFromAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Người dùng chưa được xác thực.");
        }
        return authentication.getName();
    }
}
