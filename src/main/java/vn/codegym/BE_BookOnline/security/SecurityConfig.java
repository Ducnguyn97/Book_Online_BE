package vn.codegym.BE_BookOnline.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import vn.codegym.BE_BookOnline.service.impl.UserSecurityServiceImpl;
import vn.codegym.BE_BookOnline.service.jwt.JwtFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final CorsConfigurationSource corsConfigurationSource;

    private final UserSecurityServiceImpl userSecurityService;

    // ma hoa mat khau
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // khi dang nhap thi se vao ham nay dau tien de kiem tra , tu dong goi
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // chỉ cho DaoAuthenticationProvider biết cho lưu thong tin trong database
        daoAuthenticationProvider.setUserDetailsService(userSecurityService);
        // khởi tạo phần kiểm tra password vơi user dang nhap va user trong database
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    //tao bean authencaticationManager để gọi.loadUserByUserName
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // loc duong dan khi co request gui den
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //phan quyen cho endpoint
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST).permitAll()
                        .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.ADMIN_ENDPOINT).hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
