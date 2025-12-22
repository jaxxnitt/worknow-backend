package com.worknow.backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.worknow.backend.model.User;
import com.worknow.backend.model.UserMode;
import com.worknow.backend.repository.UserRepository;
import com.worknow.backend.security.GoogleAuthService;
import com.worknow.backend.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/google")
    public Map<String, Object> googleLogin(
            @RequestBody Map<String, String> body
    ) throws Exception {

        String idToken = body.get("idToken");

        GoogleIdToken.Payload payload =
                GoogleAuthService.verify(idToken);

        String providerUserId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = userRepo
                .findByProviderAndProviderUserId("google", providerUserId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setProvider("google");
                    u.setProviderUserId(providerUserId);
                    u.setEmail(email);
                    u.setName(name);
                    u.setAvatarUrl(picture);
                    u.setCreatedAt(LocalDateTime.now());
                    u.setMode(UserMode.WORKER);
                    return userRepo.save(u);
                });

        String jwt = JwtUtil.generateToken(user.getId());

        return Map.of(
                "token", jwt,
                "user", Map.of(
                        "name", user.getName(),
                        "mode", user.getMode(),
                        "avatar", user.getAvatarUrl()
                )
        );
    }
}
