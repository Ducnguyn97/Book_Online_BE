package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.codegym.BE_BookOnline.model.User;
import vn.codegym.BE_BookOnline.repository.UserRepository;
import vn.codegym.BE_BookOnline.service.UserSecurityService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//lay user  trong CSDL
public class UserSecurityServiceImpl implements UserSecurityService {

    private final UserRepository userRepository;

    @Override
    public User findByUserEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->new UsernameNotFoundException("Không tìm User với email: "+email));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email){
        //tim user tu database
        User user = findByUserEmail(email);

        if(!user.isEnabled()){
            throw new RuntimeException("Tài khoản này đã bị khóa!!!");
        }

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNameRole()))
                .collect(Collectors.toList());
        // tra ve UserDetail chuaan trong spring
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities);
    }
}
