package vn.codegym.BE_BookOnline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// cấu hình CORS cho phép frontend truy cập API từ các nguồn khác nhau
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        //cho phep frontend truy cap
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost",        // Thêm dòng này
                "http://localhost:80",
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173"
        ));

        //cho phep cac phuong thuc
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        //cho phep cac header
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));

        //cho phép gửi credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        //thời gian cache độ sống của preflight request
        configuration.setMaxAge(3600L);

        //áp dụng cấu hình cho tất cả các endpoint
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
