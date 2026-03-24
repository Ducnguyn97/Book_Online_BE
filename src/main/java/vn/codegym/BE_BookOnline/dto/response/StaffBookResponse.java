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
public class StaffBookResponse {
    private Long id;
    private String name;
    private String author;
    private String description;
    private BigDecimal price;
    private List<String> genres;
    private List<String> imagesUrls;
    private String isbn;
    private int quantity;
    private String publisher;
}
