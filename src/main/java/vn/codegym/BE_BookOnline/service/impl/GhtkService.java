package vn.codegym.BE_BookOnline.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.codegym.BE_BookOnline.dto.response.GhtkFeeResponse;

import java.math.BigDecimal;
import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class GhtkService {
    @Value("${ghtk.api.token}")
    private String apiToken;

    @Value("${ghtk.api.base-url:https://services.giaohangtietkiem.vn}")
    private String baseUrl;

    @Value("${ghtk.shop.province}")
    private String shopProvinceRaw;

    @Value("${ghtk.shop.district}")
    private String shopDistrictRaw;

    private final RestTemplate restTemplate;

    // Mặc định 300g/cuốn sách nếu Book entity không có trường weight
    private static final int DEFAULT_WEIGHT_PER_BOOK_GRAM = 300;

    /**
     * Fix lỗi encoding UTF-8 khi Spring đọc application.properties trên Windows.
     * Nếu chuỗi bị đọc sai (ISO-8859-1), convert lại sang UTF-8.
     */
    private String fixEncoding(String raw) {
        if (raw == null) return "";
        try {
            byte[] bytes = raw.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
            String fixed = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            // Kiểm tra nếu fixed hợp lệ hơn raw (có ký tự tiếng Việt đúng)
            return fixed;
        } catch (Exception e) {
            return raw;
        }
    }

    /**
     * Tính phí ship từ GHTK
     *
     * @param toProvince   tên tỉnh/thành người nhận (VD: "Hồ Chí Minh")
     * @param toDistrict   tên quận/huyện người nhận (VD: "Quận 1")
     * @param totalWeightGram  tổng khối lượng đơn hàng (gram)
     * @param orderValue   giá trị đơn hàng (để tính phí bảo hiểm nếu cần)
     * @param transport    loại dịch vụ: "road" (tiết kiệm) hoặc "fly" (nhanh)
     * @return phí ship (VND)
     */
    public BigDecimal calculateShippingFee(
            String toProvince,
            String toDistrict,
            int totalWeightGram,
            BigDecimal orderValue,
            String transport) {

        // GHTK không nhận prefix "Thành phố", "Tỉnh", "Quận", "Huyện"...
        // provinces.open-api.vn trả về tên đầy đủ → cần normalize trước
        String normalizedProvince = normalizeLocationName(toProvince);
        String normalizedDistrict = normalizeLocationName(toDistrict);

        String url = baseUrl + "/services/shipment/fee"
                + "?pick_province=" + encode(fixEncoding(shopProvinceRaw))
                + "&pick_district=" + encode(fixEncoding(shopDistrictRaw))
                + "&province=" + encode(normalizedProvince)
                + "&district=" + encode(normalizedDistrict)
                + "&weight=" + totalWeightGram
                + "&value=" + orderValue.intValue()
                + "&transport=" + transport;
        URI uri = URI.create(url);

        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GhtkFeeResponse> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, GhtkFeeResponse.class);

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null
                    && response.getBody().isSuccess()
                    && response.getBody().getFee() != null) {

                int fee = response.getBody().getFee().getFee();
                return BigDecimal.valueOf(fee);
            }

            log.warn("GHTK trả về lỗi: {}", response.getBody());
            throw new RuntimeException("Không thể tính phí vận chuyển từ GHTK");

        } catch (Exception e) {
            log.error("Lỗi khi gọi GHTK API: {}", e.getMessage());
            throw new RuntimeException("Lỗi kết nối đến dịch vụ vận chuyển: " + e.getMessage());
        }
    }

    /**
     * Tính tổng khối lượng đơn hàng
     * Dùng giá trị mặc định 300g/cuốn vì Book entity chưa có field weight
     */
    public int calculateTotalWeight(int totalQuantity) {
        return totalQuantity * DEFAULT_WEIGHT_PER_BOOK_GRAM;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Strip prefix tỉnh/huyện trước khi gọi GHTK
     * VD: "Thành phố Hồ Chí Minh" → "Hồ Chí Minh"
     *     "Quận Hoàn Kiếm"         → "Hoàn Kiếm"
     *     "Huyện Gia Lâm"          → "Gia Lâm"
     */
    private String normalizeLocationName(String name) {
        if (name == null) return "";
        String[] prefixes = {
                "Thành phố ", "Thành Phố ", "Tỉnh ",
                "Quận ", "Huyện ", "Thị xã ", "Thị Xã "
        };
        String result = name.trim();
        for (String prefix : prefixes) {
            if (result.startsWith(prefix)) {
                result = result.substring(prefix.length()).trim();
                break;
            }
        }
        return result;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}