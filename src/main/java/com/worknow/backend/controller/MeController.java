package com.worknow.backend.controller;

import com.worknow.backend.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/me")
@CrossOrigin
public class MeController {

    @GetMapping
    public Map<String, Object> me(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        return Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "mode", user.getMode(),
                "avatar", user.getAvatarUrl()
        );
    }
}
