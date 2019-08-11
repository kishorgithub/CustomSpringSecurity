package com.example.custom.security;

import com.example.custom.model.User;
import com.example.custom.service.CustomUserDetailService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    CustomDaoAuthenticationProvider customDaoAuthenticationProvider;

    @Autowired
    CustomUserDetailService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    protected final Log logger = LogFactory.getLog(getClass());
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserDetailsChecker authenticationChecks = new DefaultPreAuthenticationChecks();
    private DaoAuthenticationProvider daoAuthenticationProvider = null;
    Authentication result = null;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = null;
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

//        loadDaoAuthenticationProvider();
        user = customDaoAuthenticationProvider.retrieveProtectedUser(name,
                (UsernamePasswordAuthenticationToken)authentication);

//      Bad credential exception check
        if (shouldAuthenticateAgainstThirdPartySystem()) {
            result = new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());

//            additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
            copyDetails(authentication, result);

            return result;
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
//        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));

        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean shouldAuthenticateAgainstThirdPartySystem() {
        return true;
    }

    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

            token.setDetails(source.getDetails());
        }
    }

    private void loadDaoAuthenticationProvider() {
        daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                logger.debug("User account is locked");

                throw new LockedException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.locked",
                        "User account is locked"));
            }

            if (!user.isEnabled()) {
                logger.debug("User account is disabled");

                throw new DisabledException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.disabled",
                        "User is disabled"));
            }

            if (!user.isAccountNonExpired()) {
                logger.debug("User account is expired");

                throw new AccountExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.expired",
                        "User account has expired"));
            }
        }
    }

}
