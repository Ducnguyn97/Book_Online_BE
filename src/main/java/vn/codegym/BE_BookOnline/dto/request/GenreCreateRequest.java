package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreCreateRequest {
    @NotBlank(message = "Tên thể loại sách không được để trống .")
    @Size(max = 50, message = "Tên thể loại sách không được vượt quá 50 ký tự.")
    private String name;
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự.")
    private String description;
}
