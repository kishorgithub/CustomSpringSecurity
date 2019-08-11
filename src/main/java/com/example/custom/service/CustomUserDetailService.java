package com.example.custom.service;

import com.example.custom.Constants;
import com.example.custom.model.CustomUserDetails;
import com.example.custom.model.Role;
import com.example.custom.model.User;
import com.example.custom.repository.RoleRepository;
import com.example.custom.repository.UserRepository;
import com.example.custom.security.CustomPasswordEncoder;
import com.example.custom.security.SuperTempInter;
import com.example.custom.security.TempInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    @Qualifier("CustomPassCode")
    PasswordEncoder passwordEncoder;

    @Autowired
    SuperTempInter tempInter;

    public int a;

    public CustomUserDetailService() {
        super();
        a = 10;
        System.out.println("Called by DaoAuthenticationProvider");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new CustomUserDetails(user);
    }

    public void saveUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setEnabled(true);
//        user.setAccountNonExpired(true);
//        user.setAccountNonLocked(true);
//        user.setCredentialsNonExpired(true);
//        Role userRole = roleRepository.findByRole("ADMIN");
//        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
//        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateFailedUserAttemps(String username) {
        User user = findByUsername(username);
        int attempts = user.getAttempts() + 1;

        if(attempts >= Constants.MAX_LOGIN_ATTEMPT_LIMIT) {
            user.setAccountNonLocked(false);
        }

        user.setAttempts(attempts);
        return userRepository.save(user);
    }

    public User resetLoginAttempts(String username) {
        User user = findByUsername(username);
        user.setAttempts(Constants.LOGIN_ATTEMPT_RESET);
        return userRepository.save(user);
    }
}
