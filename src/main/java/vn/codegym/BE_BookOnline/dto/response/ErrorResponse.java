package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;       // Ví dụ: 401, 403
    private String error;     // Ví dụ: Unauthorized
    private String message;   // Ví dụ: Email hoặc mật khẩu sai
    private LocalDateTime timestamp;
}
