package vn.codegym.BE_BookOnline.exception;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
