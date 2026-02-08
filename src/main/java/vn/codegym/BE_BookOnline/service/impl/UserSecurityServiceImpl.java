package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        //ham lay role
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String roleName = role.getNameRole();
                    if(!roleName.startsWith("ROLE_")){
                        roleName = "ROLE_" + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());
        log.info("User {} loaded with authorities: {}", email, authorities);
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
