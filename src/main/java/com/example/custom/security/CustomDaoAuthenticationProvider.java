package com.example.custom.security;

import com.example.custom.model.User;
import com.example.custom.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    CustomUserDetailService userDetailService;

    public CustomDaoAuthenticationProvider() {
        super();
        System.out.println("Dao authentication initialization");
    }

    @Autowired
//    @Qualifier("userDetailsService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Autowired
    @Qualifier("CustomPassCode")
    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication auth = null;
        String username = "";

        try {
            username = authentication.getName();

            auth = super.authenticate(authentication);

            userDetailService.resetLoginAttempts(username);

            return auth;
        } catch (BadCredentialsException e) {

            userDetailService.updateFailedUserAttemps(username);

            throw e;
        } catch (LockedException e) {
            User user = userDetailService.findByUsername(username);
            String error = username + " has been locked. Login attempt(s) " + user.getAttempts();

            throw new LockedException(error);
        }
    }

    public UserDetails retrieveProtectedUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        return retrieveUser(username, authentication);
    }



}
