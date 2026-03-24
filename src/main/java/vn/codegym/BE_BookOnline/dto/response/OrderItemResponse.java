package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long bookId;
    private String bookName;
    private String bookThumbnail;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal totalPrice;

}
