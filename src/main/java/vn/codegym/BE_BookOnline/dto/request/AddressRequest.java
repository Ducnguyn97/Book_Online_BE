package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String contactName;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-4|6-9])[0-9]{7}$", message = "Số điện thoại không hợp lệ")
    private String contactPhone;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String street;

    private Boolean isDefault;
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province;
    private Integer provinceId;
    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;
    private Integer districtId;
    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;
    private Integer wardCode;

    private String fullAddress;
    private String building;
}
