package com.example.custom.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;


public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getRoles());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
        return null;
    }
}
