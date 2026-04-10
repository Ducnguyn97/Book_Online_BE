package vn.codegym.BE_BookOnline.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class VnpayConfig {
    @Value("${vnpay.tmn-code}")
    private String tmnCode;
    @Value("${vnpay.hash-secret}")
    private String hashSecret;
    @Value("${vnpay.payment-url}")
    private String paymentUrl;
    @Value("${vnpay.return-url}")
    private String returnUrl;
    @Value("${vnpay.api-url}")
    private String apiUrl;
    @Value("${vnpay.api-version}")
    private String apiVersion;

}
