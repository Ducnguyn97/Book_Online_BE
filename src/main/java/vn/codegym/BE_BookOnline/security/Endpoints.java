package vn.codegym.BE_BookOnline.security;

public class Endpoints {
    public static final String font_end_host = "http://localhost:5173";

    //chi cho khach xem sach, anh, review
    public static final String[] PUBLIC_GET = {
            "api/books",
            "api/books/**",
            "api/genre/**",
            "api/images/**",
            "api/reviews/**",
            "api/reviews/get-review/**",
            "api/users/activate",
            "/api/vnpay/**"//call back thanh toan
    };

    public static final String[] PUBLIC_POST = {
            "api/users/register",
            "api/users/login",
            "api/users/google-login",
            "api/vnpay/create_payment",
            "api/users/forgot-password",
            "api/users/reset-password"
    };
    public static final String[] PUBLIC_PATCH = {
    };

    public static final String[] PUBLIC_PUT = {
            "api/users/update-profile",
            "api/users/change-password",

    };

    public static final String[] ADMIN_ENDPOINT = {
            "api/users/**",
            "api/books/admin/**",
            "api/feedbacks/**",
            "api/role/**",
    };
}
