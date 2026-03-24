package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdateUserRequest {
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải có từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 10, max = 15, message = "Số điện thoại phải có từ 10 đến 15 ký tự")
    private String phoneNumber;

    private String gender;

    private String username;

    private String street;
    private Integer wardCode;
    private Integer districtId;
    private Integer provinceId;
}
