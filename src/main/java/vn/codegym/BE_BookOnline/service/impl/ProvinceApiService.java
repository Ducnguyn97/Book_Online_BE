package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.response.ProvinceResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvinceApiService {
    @Value("${province.api.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate;

    /**
     * Lấy tên tỉnh/thành theo provinceCode
     * VD: 1 → "Hà Nội", 79 → "Hồ Chí Minh"
     */
    public String getProvinceName(Integer provinceCode) {
        if (provinceCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "provinceCode không được để trống");
        }
        return callApi(baseUrl + "/p/" + provinceCode, "tỉnh/thành", provinceCode);
    }

    /**
     * Lấy tên quận/huyện theo districtCode
     * VD: 268 → "Quận Hoàn Kiếm"
     */
    public String getDistrictName(Integer districtCode) {
        if (districtCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "districtCode không được để trống");
        }
        return callApi(baseUrl + "/d/" + districtCode, "quận/huyện", districtCode);
    }

    /**
     * Lấy tên phường/xã theo wardCode
     * VD: "6562" → "Phường Hàng Bạc"
     */
    public String getWardName(Integer wardCode) {
        if (wardCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "wardCode không được để trống");
        }
        return callApi(baseUrl + "/w/" + wardCode, "phường/xã", wardCode);
    }
    // -------------------------------------------------------------------------
    // Private helper — gọi API và trả về name
    // -------------------------------------------------------------------------

    private String callApi(String url, String label, Object code) {
        try {
            log.debug("Gọi Province API: {}", url);
            ResponseEntity<ProvinceResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, HttpEntity.EMPTY, ProvinceResponse.class);

            if (response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null
                    && response.getBody().getName() != null) {
                return response.getBody().getName();
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Province API trả về rỗng cho " + label + " code: " + code);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi gọi Province API [{} = {}]: {}", label, code, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Không thể lấy thông tin " + label + " (code=" + code + "): " + e.getMessage());
        }
    }
}
