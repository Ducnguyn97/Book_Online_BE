package vn.codegym.BE_BookOnline.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserResponse {

    private String fullName;

    private String phoneNumber;

    private String gender;

    private String username;

    private String street;

    private String wardcode;

    private Integer districtId;

    private Integer provinceId;
}
