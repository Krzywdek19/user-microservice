package com.krzywdek19.user_service.controller;

import com.krzywdek19.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@RequestHeader("X-User-Email") String email, @RequestHeader("Authorization") String authorizationHeader) {
        userService.deleteUserByEmail(email, authorizationHeader);
        return ResponseEntity.noContent().build();
    }
}
