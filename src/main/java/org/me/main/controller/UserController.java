package org.me.main.controller;

import lombok.RequiredArgsConstructor;
import org.me.main.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Map<String, Object> getUser() {
        return userService.getCurrentUser();
    }
}
