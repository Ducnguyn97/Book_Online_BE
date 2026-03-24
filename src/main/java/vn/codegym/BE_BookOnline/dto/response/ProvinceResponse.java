package vn.codegym.BE_BookOnline.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceResponse {
    private Integer code;
    private String name; // ten can dung de goi sang GHTK tinh phi ship
    @JsonProperty("division_type")
    private String divisionType; // "Tỉnh", "Thành phố trực thuộc trung ương"
    @JsonProperty("codename")
    private String codename;
}
