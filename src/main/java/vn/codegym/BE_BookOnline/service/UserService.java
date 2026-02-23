package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.codegym.BE_BookOnline.dto.request.*;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.CustomerListResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
import vn.codegym.BE_BookOnline.model.User;

public interface UserService {
    User registerUser(UserRegisterRequest request);

    AuthResponse loginWithLocal(UserLoginRequest request);

    AuthResponse loginWithGoogle(GoogleLoginRequest request);

    UpdateUserResponse updateUser(String email, UpdateUserRequest request);

    UserProfile getUserProfile(String email);//get user profile

    UserProfile lockUserAccount(Long UserId, LockUserRequest request);//lock user account

    UserProfile unlockUserAccount(Long UserId, UnlockUserRequest request);//unlock user account

    void changeUserPassword(String email, String newPassword, String oldPassword);//update password

    void changeUserAvatar(String email, String avatarUrl);//update avatar link

    void initiateForgotPassword(String email);//send email with token

    void completeForgotUserPassword( String newPassword, String token);//reset password using token

    User verifyAccount(String token);// xac thuc tai khoan

    Page<CustomerListResponse> getAllCustomers(Pageable pageable);// lay danh sach khach hang voi phan trang

}
