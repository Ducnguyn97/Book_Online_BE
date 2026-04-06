package vn.codegym.BE_BookOnline.security;

public class Endpoints {
    public static final String FRONTEND_HOST = "http://localhost:5173";

    //chi cho khach xem sach, anh, review
    public static final String[] PUBLIC_GET = {
            "/api/books",
            "/api/books/**",
            "/api/genre/**",
            "/api/images/**",
            "/api/reviews/**",
            "/api/reviews/get-review/**",
            "/api/users/activate",
            "/api/vnpay/**",//call back thanh toan
    };

    public static final String[] PUBLIC_POST = {
            "/api/users/register",
            "/api/users/login",
            "/api/users/google-login",
            "/api/vnpay/create_payment",
            "/api/users/forgot-password",
            "/api/users/reset-password"
    };
    public static final String[] PUBLIC_PATCH = {
    };

    public static final String[] PUBLIC_PUT = {
    };

    public static final String[] ADMIN_ENDPOINT = {
            "/api/admin/**",
            "/api/books/admin/**",
            "/api/feedbacks/**",
            "/api/role/**",

    };
    public static final String[] STAFF_ENDPOINT = {
            "/api/staff/books/**",
                "/api/staff/genres/**",
                "/api/staff/orders/**"

    };
}
