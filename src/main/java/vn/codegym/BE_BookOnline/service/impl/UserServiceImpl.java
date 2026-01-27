package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.codegym.BE_BookOnline.dto.request.UpdateUserRequest;
import vn.codegym.BE_BookOnline.dto.request.UserLoginRequest;
import vn.codegym.BE_BookOnline.dto.request.UserRegisterRequest;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
import vn.codegym.BE_BookOnline.exception.EmailNotVerifiedException;
import vn.codegym.BE_BookOnline.exception.InvalidCredentialsException;
import vn.codegym.BE_BookOnline.exception.ResourceNotFoundException;
import vn.codegym.BE_BookOnline.exception.UserAlreadyExistsException;
import vn.codegym.BE_BookOnline.model.Role;
import vn.codegym.BE_BookOnline.model.User;
import vn.codegym.BE_BookOnline.repository.RoleRepository;
import vn.codegym.BE_BookOnline.repository.UserRepository;
import vn.codegym.BE_BookOnline.service.EmailService;
import vn.codegym.BE_BookOnline.service.UserService;
import vn.codegym.BE_BookOnline.service.jwt.JwtService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    @Override
    @Transactional
    public User registerUser(UserRegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailNotVerifiedException("Email đã được đăng ký vui lòng sử dụng email khác.");
        }

        if(userRepository.existsByUsername(request.getUserName())){
            throw new UserAlreadyExistsException("Tên người dùng đã được sử dụng vui lòng sử dụng tên khác.");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new InvalidCredentialsException("Mật khẩu và xác nhận mật khẩu không trùng khớp vui lòng nhập lại. ");
        }

        String token = UUID.randomUUID().toString();
        //role mac dinh user
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByNameRole("CUSTOMER"));
        request.setRoles(roles);

        User newUser = User.builder()
                .email(request.getEmail())
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .enabled(false)
                .emailVerified(false)
                .verificationCode(token)
                .build();

        User saveUser = userRepository.save(newUser);

        try{
            String recipientName = saveUser.getUsername() != null
                    ?saveUser.getUsername()
                    :saveUser.getEmail();
            emailService.sendVerificationEmail(
                    saveUser.getEmail(),
                    recipientName,
                    token
            );
        }catch (Exception e){
            System.err.println(" Lỗi gửi email: "+e.getMessage());
        }
        return saveUser;
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        // buoc nay tu dong gọi UserSecurityService.loadUserByUsername()
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )// object DTO cua spring security chứa Email va password mà người dùng gửi lên
        );
        // Luu thong tin vao security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // lay UserDetails object da duoc xac thuc
        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        // lay lai user tu DB de lay cac thong tin khac
        User user = userRepository.findByEmail(springUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ..."));

        String token = jwtService.generateToken(user.getEmail());

        List<String> roleNames = user.getRoles().stream()//tao luong role
                .map(Role::getNameRole)// role -> String
                .toList();// gom thanh list

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(roleNames)
                .userId(user.getId())
                .build();

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

    @Override
    public User verifyAccount(String token) {
        //tim user bằng token
        User user = userRepository.findByVerificationCode(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ hoặc không tồn tại. "));
        // kiem tra xem user duoc kich hoat chua
        if(Boolean.TRUE.equals(user.isEnabled())){
            return user;
        }

        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setVerificationCode(null);

        userRepository.save(user);

        return user;
    }
}
