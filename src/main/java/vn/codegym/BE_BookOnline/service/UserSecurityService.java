package vn.codegym.BE_BookOnline.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import vn.codegym.BE_BookOnline.model.User;

public interface UserSecurityService extends UserDetailsService {
    public User findByUserEmail(String email);
}
