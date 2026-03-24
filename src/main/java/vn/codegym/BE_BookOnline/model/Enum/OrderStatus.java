package vn.codegym.BE_BookOnline.model.Enum;

public enum OrderStatus {
    PENDING,      // Chờ xác nhận
    PROCESSING,   // Đang xử lý
    SHIPPING,     // Đang giao hàng
    COMPLETED,    // Hoàn thành
    CANCELLED     // Đã hủy
}
