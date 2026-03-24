package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.request.OrderRequest;
import vn.codegym.BE_BookOnline.dto.response.*;
import vn.codegym.BE_BookOnline.model.*;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;
import vn.codegym.BE_BookOnline.model.Enum.PaymentStatus;
import vn.codegym.BE_BookOnline.repository.*;
import vn.codegym.BE_BookOnline.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final GhtkService ghtkService;
    private final ProvinceApiService provinceApiService;

    @Override
    @Transactional(readOnly = true)
    public CheckoutResponse getCheckoutInfo(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Address address = addressRepository.findByIdAddressAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với id: " + addressId));
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giỏ hàng đang trống");
        }

        //Build danh sach san pham
        List<CheckoutItemResponse> items = cartItems.stream()
                .map(this::mapToCheckoutItemResponse)
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CheckoutItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalQuantity = items.stream().mapToInt(CheckoutItemResponse::getQuantity).sum();
        int totalWeight = ghtkService.calculateTotalWeight(totalQuantity);

        // Resolve tên tỉnh/huyện/xã 1 lần — dùng chung cho cả tính phí ship và snapshot địa chỉ
        String provinceName = resolveProvinceName(address.getProvinceId());
        String districtName = resolveDistrictName(address.getDistrictId());
        String wardName     = resolveWardName(address.getWardCode());

        // tính phí ship cho từng loại dịch vụ giao hàng — tên đã resolve, không gọi API lại
        List<Delivery> deliveries = deliveryRepository.findByActiveTrue();
        List<DeliveryOptionResponse> deliveryOptionResponses = deliveries.stream()
                .map(delivery -> {
                    try {
                        BigDecimal shippingFee = ghtkService.calculateShippingFee(
                                provinceName,
                                districtName,
                                totalWeight,
                                subtotal,
                                delivery.getGhtkServiceName()
                        );
                        return DeliveryOptionResponse.builder()
                                .deliveryId(delivery.getIdDelivery())
                                .name(delivery.getNameDelivery())
                                .fee(shippingFee)
                                .description(delivery.getDescriptionDelivery())
                                .build();
                    } catch (Exception e) {
                        log.warn("Không tính được phí ship cho dịch vụ {}: {}", delivery.getNameDelivery(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)//loc ket qua null (trường hợp GHTK lỗi)
                .toList();
        List<Payment> payments = paymentRepository.findByActiveTrue();
        List<PaymentOptionResponse> paymentOptionResponses = payments.stream()
                .map(payment -> PaymentOptionResponse.builder()
                        .paymentId(payment.getIdPayment())
                        .name(payment.getNamePayment())
                        .fee(payment.getFeePayment())
                        .description(payment.getDescriptionPayment())
                        .active(payment.isActive())
                        .build())
                .toList();
        BigDecimal defaultShippingFee = deliveryOptionResponses.stream()
                .map(DeliveryOptionResponse::getFee)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        return CheckoutResponse.builder()
                .shippingAddress(toCheckoutAddressResponse(address, wardName, districtName, provinceName))
                .items(items)
                .totalItems(totalQuantity)
                .subtotal(subtotal)
                .shippingFee(defaultShippingFee) // Gợi ý phí ship thấp nhất
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(subtotal.add(defaultShippingFee))
                .deliveryOptions(deliveryOptionResponses)
                .paymentMethods(paymentOptionResponses)
                .estimatedDeliveryDate(null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateShippingFee(String email, Long addressId, Long deliveryId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Address address = addressRepository.findByIdAddressAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với id: " + addressId));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dịch vụ giao hàng không hợp lệ"));
        if(!delivery.isActive()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Dịch vụ giao hàng không còn khả dụng. ");
        }
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giỏ hàng đang trống");
        }
        int totalQuantity = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        int totalWeight = ghtkService.calculateTotalWeight(totalQuantity);

        BigDecimal subtotal = cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ghtkService.calculateShippingFee(
                resolveProvinceName(address.getProvinceId()),
                resolveDistrictName(address.getDistrictId()),
                totalWeight,
                subtotal,
                delivery.getGhtkServiceName()
        );
    }

    @Override
    @Transactional
    public OrderResponse createOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Address address = addressRepository.findByIdAddressAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với id: " + request.getAddressId()));
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phương thức thanh toán không hợp lệ"));
        if (!payment.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Phương thức thanh toán không còn khả dụng. ");
        }
        Delivery delivery = deliveryRepository.findById(request.getDeliveryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dịch vụ giao hàng không hợp lệ"));
        if(!delivery.isActive()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Dịch vụ giao hàng không còn khả dụng. ");
        }
        //lấy danh sách items cần đặt
        List<CartItem> itemsToOrder = resolveOrderItems(user, request);
        if (itemsToOrder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có sản phẩm nào để đặt hàng");
        }
        //tính subtotal và tổng khối lượng
        BigDecimal subtotal = itemsToOrder.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalQuantity = itemsToOrder.stream().mapToInt(CartItem::getQuantity).sum();
        int totalWeight = ghtkService.calculateTotalWeight(totalQuantity);

        itemsToOrder.forEach(item -> validateStock(item.getBook(), item.getQuantity()));

        String provinceName = resolveProvinceName(address.getProvinceId());
        String districtName = resolveDistrictName(address.getDistrictId());
        String wardName     = resolveWardName(address.getWardCode());

        BigDecimal shippingFee = ghtkService.calculateShippingFee(
                provinceName,
                districtName,
                totalWeight,
                subtotal,
                delivery.getGhtkServiceName()
        );

        //tao order
        BigDecimal totalAmount = subtotal.add(shippingFee);
        Order order = Order.builder()
                .user(user)
                .payment(payment)
                .delivery(delivery)
                .orderDate(LocalDateTime.now())
                .deliveryAddress(buildFullAddress(address.getStreet(),provinceName, districtName, wardName ))
                .phoneNumberCustomer(address.getContactPhone())
                .recipientName(address.getContactName())
                .totalPriceProducts(subtotal)
                .feeDelivery(shippingFee)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .note(request.getNote())
                .build();

        // tao OrderDetail
        List<OrderDetail> orderDetails = itemsToOrder.stream()
                .map(item -> {
                    Book book = item.getBook();
                    return OrderDetail.builder()
                            .order(order)
                            .book(book)
                            .productName(book.getNameBook()) // snapshot tên sản phẩm
                            .thumbnailAtPurchase(getFirstImage(book)) // snapshot ảnh sản phẩm
                            .priceAtPurchase(item.getPrice()) // snapshot giá tại thời điểm đặt hàng
                            .quantity(item.getQuantity())
                            .build();
                })
                .toList();
        order.getOrderDetails().addAll(orderDetails);

        itemsToOrder.forEach(item -> {
            Book book = item.getBook();
            book.setQuantityBook(book.getQuantityBook() - item.getQuantity());
            book.setSoldQuantityBook(book.getSoldQuantityBook() + item.getQuantity());
            bookRepository.save(book);
        });

        if(!Boolean.TRUE.equals(request.getBuyNow())){
            Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng cho user: " + email));
            cart.clearCart();
            cartRepository.save(cart);
        }
        Order savedOrder = orderRepository.save(order);
        log.info("Tạo đơn hàng thành công: orderId={}, user={}, total={}",
                savedOrder.getIdOrder(), email, totalAmount);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId, String reason) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy đơn hàng với id: " + orderId));
        if(order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.PROCESSING){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ được phép hủy đơn hàng khi trạng thái là PENDING hoặc PROCESSING. Trạng thái hiện tại: " + order.getOrderStatus());
        }
        // hoan tra lai ton kho
        order.getOrderDetails().forEach(detail -> {
            Book book = detail.getBook();
            book.setQuantityBook(book.getQuantityBook() + detail.getQuantity());
            book.setSoldQuantityBook(Math.max(0, book.getSoldQuantityBook() - detail.getQuantity()));
            bookRepository.save(book);
        });
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancelAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    private CheckoutAddressResponse toCheckoutAddressResponse(
            Address address, String wardName, String districtName, String provinceName) {
        return CheckoutAddressResponse.builder()
                .addressId(address.getIdAddress())
                .contactName(address.getContactName())
                .contactPhone(address.getContactPhone())
                .fullAddress(buildFullAddress(address.getStreet(), wardName, districtName, provinceName))
                .isDefault(address.getIsDefault())
                .build();
    }

    private CheckoutItemResponse mapToCheckoutItemResponse(CartItem cartItem) {
        return CheckoutItemResponse.builder()
                .bookId(cartItem.getBook().getId())
                .bookName(cartItem.getBook().getNameBook())
                .bookThumbnail(cartItem.getBook().getImageUrls() != null && !cartItem.getBook().getImageUrls().isEmpty()
                        ? cartItem.getBook().getImageUrls().get(0) : "")
                .unitPrice(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();
    }

    private void validateStock(Book book, int requestedQuantity) {
        if (book.getQuantityBook() < requestedQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Sách '%s' chỉ còn %d cuốn trong kho.",
                            book.getNameBook(), book.getQuantityBook()));
        }
    }

    private String buildFullAddress(String street, String wardName,
                                    String districtName, String provinceName) {
        return String.join(", ", street, wardName, districtName, provinceName);
    }

    private String resolveDistrictName(Integer districtId) {
        return provinceApiService.getDistrictName(districtId);
    }

    private String resolveProvinceName(Integer provinceId) {
        return provinceApiService.getProvinceName(provinceId);
    }
    private String resolveWardName(Integer wardCode) {
        return provinceApiService.getWardName(wardCode);
    }
    /**
     * Xác định danh sách CartItem cần đặt:
     * - buyNow = true  → tạo CartItem tạm từ bookId + quantity
     * - buyNow = false → lấy toàn bộ cart của user
     */
    private List<CartItem> resolveOrderItems(User user, OrderRequest request) {
        if(Boolean.TRUE.equals(request.getBuyNow())){
            if(request.getBookId() == null || request.getQuantity() == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Thiếu thông tin bookId hoặc quantity cho buyNow");
            }
            Book book = bookRepository.findById(request.getBookId()).
                    orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy sách với id: " + request.getBookId()));
            CartItem buyNowItem = CartItem.builder()
                    .book(book)
                    .quantity(request.getQuantity())
                    .price(book.getPriceBook())
                    .build();
            return List.of(buyNowItem);
        }
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy giỏ hàng cho user: " + user.getEmail()));
        return cart.getCartItems();
    }
    private String getFirstImage(Book book) {
        List<String> images = book.getImageUrls();
        return (images != null && !images.isEmpty()) ? images.get(0) : "";
    }




    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException
                        (HttpStatus.NOT_FOUND,"Không tìm thấy đơn hàng với id: " + orderId));
        return mapToOrderResponse(order);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Không tìm User với email: " + email));
        Page<Order> orders = orderRepository.findByUserIdWithDetails(user.getId(), pageable);
        return orders.map(this::mapToOrderResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResponseStatusException
                        (HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng với id: " + orderId));
        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(newStatus);
            if (newStatus == OrderStatus.COMPLETED) {
                order.setCompleteAt(LocalDateTime.now());
                order.setPaymentStatus(PaymentStatus.PAID);
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái đơn hàng không hợp lệ: " + status);
        }
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderDetails().stream()
                .map(item -> OrderItemResponse.builder()
                        .bookId(item.getBook().getId())
                        .bookThumbnail(item.getThumbnailAtPurchase())
                        .bookName(item.getProductName())
                        .quantity(item.getQuantity())
                        .priceAtOrder(item.getPriceAtPurchase())
                        .totalPrice(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();
        Integer totalItems = items.stream()
                .mapToInt(OrderItemResponse::getQuantity)
                .sum();
        return OrderResponse.builder()
                .orderNumber(order.getIdOrder()) // Sử dụng idOrder làm orderNumber tạm thời
                .status(order.getOrderStatus())
                .paymentMethod(order.getPayment().getNamePayment())
                .paymentStatus(order.getPaymentStatus())
                .address(order.getDeliveryAddress())
                .customerName(order.getRecipientName())
                .customerPhone(order.getPhoneNumberCustomer())
                .items(items)
                .totalItems(totalItems)
                .subTotal(order.getTotalPriceProducts())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getFeeDelivery())
                .totalPrice(order.getTotalAmount())
                .reason(order.getCancellationReason())
                .createdAt(order.getOrderDate())
                .completedAt(order.getCompleteAt())
                .canceledAt(order.getCancelAt())
                .build();
    }
}
