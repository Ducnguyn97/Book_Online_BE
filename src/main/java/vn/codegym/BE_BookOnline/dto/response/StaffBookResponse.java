package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.codegym.BE_BookOnline.model.Enum.BookStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private Double discountBook;
    private Double averageRating;
    private BookStatus bookStatus;
    private LocalDateTime createdAt;
    private String createdBy;
}
