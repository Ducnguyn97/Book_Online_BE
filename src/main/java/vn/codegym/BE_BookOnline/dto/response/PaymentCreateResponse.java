package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCreateResponse {
    private String apiVesion;
    private String vnpayCommand;
    private String tmnCode;
    private Long amount;
    private String currency;
    private Long orderId;
    private String orderInfo;
    private String OrderType;
    private String locale;
    private String returnUrl;
    private String ipAddress;
    private String createDate;
    private String expireDate;
}
