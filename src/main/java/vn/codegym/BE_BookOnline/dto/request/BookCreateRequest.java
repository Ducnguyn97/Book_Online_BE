package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.*;
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
public class BookCreateRequest {

    @NotBlank(message = "Tên sách không được để trống.")
    @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Tên tác giả không để trống.")
    @Size(max = 50, message = "Tên tác giả không vượt quá 50 ký tự.")
    private String author;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự.")
    private String description;

    @NotNull(message = "Giá tiền không được để trống")
    @DecimalMin(value = "0.00", inclusive = true, message = "Giá tiền phải lớn hơn hoặc bằng 0")
    @Digits(integer = 12, fraction = 2, message = "Giá tiền không hợp lệ")
    private BigDecimal price;

    @NotEmpty(message = "Cần chọn ít nhất một thể loại cho sách")
    private List<Long> genreIds;

    private List<String> imageUrls;

    @NotBlank(message = "ISBN không được để trống.")
    private String isbn;

    @NotNull(message = "Số lượng không được để trống.")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer quantity;

    @NotBlank(message = "Nhà xuất bản không để trống.")
    @Size(max = 50, message = "Tên NXB không vượt quá 50 ký tự.")
    private String publisher;

}
