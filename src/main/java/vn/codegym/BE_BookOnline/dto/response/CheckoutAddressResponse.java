package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutAddressResponse {
    private Long addressId;
    private String contactName;
    private String contactPhone;
    private String fullAddress;         // chuỗi ghép đầy đủ để hiển thị
    private Boolean isDefault;
}
