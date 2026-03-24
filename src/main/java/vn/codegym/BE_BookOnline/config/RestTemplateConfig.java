package vn.codegym.BE_BookOnline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
//Cấu hinh RestTemplate để goi API bên ngoài, có thể set timeout để tránh treo ứng dụng khi API bên ngoài không phản hồi
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 5 seconds
        factory.setReadTimeout(10000); // 5 seconds
        return new RestTemplate(factory);
    }
}
