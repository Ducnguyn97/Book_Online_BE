package vn.codegym.BE_BookOnline.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String s) {
        super(s);
    }
}
