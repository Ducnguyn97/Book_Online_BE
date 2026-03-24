package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDetailsResponse {
    private Long id;
    private String name;
    private String author;
    private String description;
    private List<String> genre;
    private String publisher;
    private List<String> image;
    private BigDecimal price;
    private Integer quantity;
    private String discount;
    private Integer soldQuantityBook;
    private  Double rating;
}
