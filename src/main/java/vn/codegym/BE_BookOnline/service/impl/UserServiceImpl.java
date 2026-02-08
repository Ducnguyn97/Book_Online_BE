package vn.codegym.BE_BookOnline.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.codegym.BE_BookOnline.dto.request.*;
import vn.codegym.BE_BookOnline.dto.response.AuthResponse;
import vn.codegym.BE_BookOnline.dto.response.UpdateUserResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
import vn.codegym.BE_BookOnline.exception.EmailNotVerifiedException;
import vn.codegym.BE_BookOnline.exception.InvalidCredentialsException;
import vn.codegym.BE_BookOnline.exception.ResourceNotFoundException;
import vn.codegym.BE_BookOnline.exception.UserAlreadyExistsException;
import vn.codegym.BE_BookOnline.model.Address;
import vn.codegym.BE_BookOnline.model.Enum.AuthProvider;
import vn.codegym.BE_BookOnline.model.Role;
import vn.codegym.BE_BookOnline.model.User;
import vn.codegym.BE_BookOnline.repository.RoleRepository;
import vn.codegym.BE_BookOnline.repository.UserRepository;
import vn.codegym.BE_BookOnline.service.EmailService;
import vn.codegym.BE_BookOnline.service.UserService;
import vn.codegym.BE_BookOnline.service.jwt.JwtService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserServiceImpl implements UserService {

    @Value("${google.client.id}")
    private String googleClientId;

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
                .authProvider(AuthProvider.LOCAL)
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
    public AuthResponse loginWithLocal(UserLoginRequest request) {
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

   // =====================================================================
    // LOGIN GOOGLE
    // =====================================================================
    @Override
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        String rawToken = request.getIdToken();

        // ---------- validate input ------------------------------------------
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new InvalidCredentialsException("ID Token từ Google trống.");
        }
        rawToken = rawToken.trim();

        GoogleIdToken idToken = verifyGoogleToken(rawToken);

        // ---------- lấy thông tin từ payload ------------------------------------
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email     = payload.getEmail();
        String username  = (String) payload.get("name");
        String avatarUrl = (String) payload.get("picture");

        // ---------- tìm hoặc tạo user ------------------------------------------
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email, username, avatarUrl));

        // ---------- issue JWT --------------------------------------------------
        String token = jwtService.generateToken(user.getEmail());

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getNameRole)
                .toList();

        return AuthResponse.builder()
                .token(token)
                .roles(roleNames)
                .email(email)
                .username(username)
                .userId(user.getId())
                .build();
    }

    /**
     * Xác thực Google ID Token
     */
    private GoogleIdToken verifyGoogleToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new InvalidCredentialsException(
                        "Xác thực Google thất bại. Token không hợp lệ hoặc đã hết hạn."
                );
            }

            return idToken;

        } catch (GeneralSecurityException e) {
            log.error("Lỗi bảo mật khi xác thực Google token: {}", e.getMessage());
            throw new InvalidCredentialsException("Xác thực Google thất bại: " + e.getMessage());
        } catch (IOException e) {
            log.error("Lỗi IO khi xác thực Google token: {}", e.getMessage());
            throw new InvalidCredentialsException("Không thể kết nối đến Google để xác thực.");
        } catch (IllegalArgumentException e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
            throw new InvalidCredentialsException("Token không đúng định dạng.");
        }
    }

    /**
     * Helper: tạo User mới khi đăng nhập Google lần đầu.
     * - Không cần password (đặt 1 giá trị random đã encode để pass DB constraint)
     * - Auto-activate (enabled = true, emailVerified = true) vì Google đã verify email
     * - authProvider = GOOGLE
     */
    private User createGoogleUser(String email, String name, String avatarUrl) {
        String baseUsername = email.split("@")[0];
        String username = userRepository.existsByUsername(baseUsername)
                ?baseUsername + "_" + UUID.randomUUID().toString().substring(0,8)
                : baseUsername;
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByNameRole("CUSTOMER"));

        User newUser = new User().builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .avatar(avatarUrl)
                .roles(roles)
                .fullName(name)
                .enabled(true)
                .emailVerified(true)
                .authProvider(AuthProvider.GOOGLE)
                .build();
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public UpdateUserResponse updateUser(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        updateDefaultAddressForUser(user, request);

        userRepository.save(user);

        return UpdateUserResponse.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .street(request.getStreet())
                .provinceId(request.getProvinceId())
                .districtId(request.getDistrictId())
                .wardcode(request.getWardcode())
                .build();
    }

    // logic: tim hoac tao dia chi mac dinh cho user neu chua co
    private void updateDefaultAddressForUser(User user, UpdateUserRequest request) {
        Address addressToUpdate = user.getAddresses().stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElseGet(()-> {
                    Address newAddress = new Address();
                    newAddress.setUser(user);
                    newAddress.setIsDefault(true);
                    if(user.getAddresses() == null) {
                        user.setAddresses(new ArrayList<>());
                    }
                    user.getAddresses().add(newAddress);
                    return newAddress;
                });
        addressToUpdate.setStreet(request.getStreet());
        addressToUpdate.setContactName(request.getFullName());
        addressToUpdate.setContactPhone(request.getPhoneNumber());
        addressToUpdate.setProvinceId(request.getProvinceId());
        addressToUpdate.setDistrictId(request.getDistrictId());
        addressToUpdate.setWardCode(request.getWardcode());
    }

    @Override
    public UserProfile getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
       return mapToUserProfile(user);
    }

    @Override
     public UserProfile lockUserAccount(Long userId, LockUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
            user.lockUser(request.getReason());
            emailService.sendAccountLockEmail(
                    user.getEmail(),
                    user.getFullName(),
                    request.getReason());
        userRepository.save(user);
        return mapToUserProfile(user);

    }

    public UserProfile mapToUserProfile(User user) {
        String defaultAddress = user.getAddresses().stream()
                .filter(Address::getIsDefault)
                .map(Address::getStreet)
                .findFirst()
                .orElse(user.getAddresses().isEmpty() ? null : user.getAddresses().get(0).getStreet());
        return UserProfile.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(defaultAddress)
                .gender(user.getGender())
                .build();
    }


    @Override
    public UserProfile unlockUserAccount(Long UserId, UnlockUserRequest request) {
        User user = userRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
        user.unlockUser();
        emailService.sendAccountUnlockEmail(
                user.getEmail(),
                user.getFullName()
        );
        userRepository.save(user);
        return mapToUserProfile(user);
    }


    @Override
    public void changeUserPassword(String email, String newPassword, String oldPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Mật khẩu cũ không đúng vui lòng nhập lại.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void changeUserAvatar(String email, String avatarUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void initiateForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ..."));
        String token= UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordExpiredAt(LocalDateTime.now().plusMinutes(15)); // token hợp lệ trong 15 phút
        userRepository.save(user);
        emailService.sendForgotPasswordEmail(
                user.getEmail(),
                user.getFullName(),
                token
        );
    }

    @Override
    @Transactional
    public void completeForgotUserPassword( String newPassword, String token) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguời dùng ..."));
        // kiem tra token
        if(user.getResetPasswordToken() == null || !user.getResetPasswordToken().equals(token)){
            throw new InvalidCredentialsException("Token không hợp lệ hoặc không tồn tại. ");
        }// kiem tra token het han
        if(user.getResetPasswordExpiredAt() == null || user.getResetPasswordExpiredAt().isBefore(LocalDateTime.now()) ){
            throw new InvalidCredentialsException("Token đã hết hạn. Vui lòng thực hiện lại quy trình quên mật khẩu. ");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiredAt(null);
        userRepository.save(user);
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
