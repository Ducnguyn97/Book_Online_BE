package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.CancelOrderRequest;
import vn.codegym.BE_BookOnline.dto.request.OrderRequest;
import vn.codegym.BE_BookOnline.dto.response.CheckoutResponse;
import vn.codegym.BE_BookOnline.dto.response.OrderResponse;
import vn.codegym.BE_BookOnline.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    //lay thong tin thanh toan
    @GetMapping("/checkout-info")
    public ResponseEntity<CheckoutResponse> getCheckoutInfo(Authentication authentication,
                                                             @RequestParam Long addressId){
        String email = authentication.getName();
        CheckoutResponse response = orderService.getCheckoutInfo(email, addressId);
        return ResponseEntity.ok(response);
    }
    //lay danh sach don hang cua user
    @GetMapping()
    public ResponseEntity<Page<OrderResponse>> getUserOrders(Authentication authentication,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getUserOrders(email, pageRequest);
        return ResponseEntity.ok(orders);
    }
    //lay thong tin don hang theo id
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId, Authentication authentication) {
        String email = authentication.getName();
        OrderResponse order = orderService.getOrderDetails(email, orderId);
        return ResponseEntity.ok(order);
    }
    //tao don hang
    @PostMapping()
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        String email = authentication.getName();
        OrderResponse order = orderService.createOrder(email, request);
        return ResponseEntity.ok(order);
    }
    //huy don hang
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId, @Valid @RequestBody CancelOrderRequest request, Authentication authentication) {
        String email = authentication.getName();
        OrderResponse order = orderService.cancelOrder(email, orderId, request.getReason());
        return ResponseEntity.ok(order);
    }
}
