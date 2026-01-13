package vn.codegym.BE_BookOnline.service.impl;

import vn.codegym.BE_BookOnline.dto.request.UpdateUserRequest;
import vn.codegym.BE_BookOnline.dto.request.UserLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UserRegisterRequest;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
import vn.codegym.BE_BookOnline.dto.response.UserRegisterResponse;
import vn.codegym.BE_BookOnline.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        return null;
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        return null;
    }

    @Override
    public UpdateUserResponse updateUser(String email, UpdateUserRequest request) {
        return null;
    }

    @Override
    public UserProfile getUserProfile(String email) {
        return null;
    }

    @Override
    public void lockUserAccount(String email) {

    }

    @Override
    public void changeUserPassword(String email, String newPassword, String oldPassword) {

    }

    @Override
    public void changeUserAvatar(String email, String avatarUrl) {

    }

    @Override
    public void initiateForgotPassword(String email) {

    }

    @Override
    public void completeForgotUserPassword(String email, String newPassword, String token) {

    }
}
