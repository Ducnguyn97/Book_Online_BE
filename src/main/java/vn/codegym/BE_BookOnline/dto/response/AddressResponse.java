package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String contactName;
    private String contactPhone;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String building;
    private Boolean isDefault;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardCode;
    private String fullAddress;
    private String addressType;

    public String buildFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            sb.append(street);
        }
        if (building != null && !building.isEmpty()) {
            sb.append(", ").append(building);
        }
        if (ward != null && !ward.isEmpty()) {
            sb.append(", ").append(ward);
        }
        if (district != null && !district.isEmpty()) {
            sb.append(", ").append(district);
        }
        if (province != null && !province.isEmpty()) {
            sb.append(", ").append(province);
        }
        return sb.toString();
    }
}
