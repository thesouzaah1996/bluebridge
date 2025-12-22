package com.blue.bridge.users.controller;

import com.blue.bridge.res.Response;
import com.blue.bridge.users.dto.UpdatePasswordRequest;
import com.blue.bridge.users.dto.UserDTO;
import com.blue.bridge.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getMyUserDetails() {
        return ResponseEntity.ok(userService.getMyUserDetails());
    }

    @GetMapping("/by-id/{userId}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/update-password")
    public ResponseEntity<Response<?>> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest) {
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequest));
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<Response<?>> getMyUserDetails(@RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePicture(file));
    }
}
