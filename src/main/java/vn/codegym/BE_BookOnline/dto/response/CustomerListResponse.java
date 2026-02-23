package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerListResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String gender;
    private String avatarUrl;
    private String email;
    private String username;
    private String RejectReason;
    private boolean enabled;
    private LocalDateTime lockedAt;



}
