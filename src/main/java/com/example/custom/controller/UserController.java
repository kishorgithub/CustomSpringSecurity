package com.example.custom.controller;

import com.example.custom.config.JwtTokenProvider;
import com.example.custom.model.User;
import com.example.custom.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class UserController {

    @Autowired
    CustomUserDetailService userDetailService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;


    @GetMapping("/custom/secure/map")
    public String getUser() {
        return "Custom Mapping";
    }

    @GetMapping("/custom/secure")
    public ResponseEntity secureCall() {
        User user = userDetailService.findByUsername("rahu");
        String token = jwtTokenProvider.createToken("rahu", user.getRoles());
        Map<Object, Object> model = new HashMap<>();
        model.put("username", "rahu");
        model.put("token", token);
        return ok(model);
//        return "Secure Mapping";
    }

    @PostMapping("/custom/register")
    public ResponseEntity register(@RequestBody User user) {
        User userExists = userDetailService.findByUsername(user.getUsername());
        if (userExists != null) {
            throw new BadCredentialsException("User with username: " + user.getEmail() + " already exists");
        }
        userDetailService.saveUser(user);
        Map<Object, Object> model = new HashMap<>();
        model.put("message", "User registered successfully");
        return ok(model);
    }

}
