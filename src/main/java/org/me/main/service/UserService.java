package org.me.main.service;

import lombok.RequiredArgsConstructor;
import org.me.main.model.User;
import org.me.main.model._enum.Status;
import org.me.main.repo.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public Map<String, Object> getCurrentUser() {
        Map<String, Object> userInfo = new HashMap<>();
        var context = SecurityContextHolder.getContext().getAuthentication();

        if (context instanceof OAuth2AuthenticationToken authentication) {
            OAuth2User oAuth2User = authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");
            userInfo.put("name", name);
            userInfo.put("email", email);
            userInfo.put("picture", picture);

            createUser(name, email);
        }

        return userInfo;
    }

    private void createUser(String name, String email) {
        Optional<User> userExist = userRepo.getUserByEmail(email);

        if (userExist.isEmpty()) {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setStatus(Status.ACTIVE);
            userRepo.save(newUser);
        }
    }
}
