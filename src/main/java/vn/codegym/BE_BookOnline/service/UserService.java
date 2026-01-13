package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.UpdateUserRequest;
import vn.codegym.BE_BookOnline.dto.request.UserLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UserRegisterRequest;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
import vn.codegym.BE_BookOnline.dto.response.UserRegisterResponse;
import vn.codegym.BE_BookOnline.model.User;

public interface UserService {
    UserRegisterResponse registerUser(UserRegisterRequest request);

    AuthResponse loginUser(UserLoginRequest request);

    UpdateUserResponse updateUser(String email, UpdateUserRequest request);

    UserProfile getUserProfile(String email);//get user profile

    void lockUserAccount(String email);//lock user account

    void changeUserPassword(String email, String newPassword, String oldPassword);//update password

    void changeUserAvatar(String email, String avatarUrl);//update avatar link

    void initiateForgotPassword(String email);//send email with token

    void completeForgotUserPassword(String email, String newPassword, String token);//reset password using token
}
