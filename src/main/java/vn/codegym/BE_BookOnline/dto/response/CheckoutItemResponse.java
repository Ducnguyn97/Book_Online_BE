package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItemResponse {
    private Long bookId;
    private String bookName;
    private String bookThumbnail;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;      // = unitPrice * quantity
}
