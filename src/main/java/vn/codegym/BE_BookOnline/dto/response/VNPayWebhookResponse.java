package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VNPayWebhookResponse {
    private String txnRef;
    private String vnpAmount;
    private String vnpResponseCode;
    private String vnpTransactionNo;
}
