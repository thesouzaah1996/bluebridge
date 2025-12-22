package com.blue.bridge.users.service;

import com.blue.bridge.res.Response;
import com.blue.bridge.users.dto.UpdatePasswordRequest;
import com.blue.bridge.users.dto.UserDTO;
import com.blue.bridge.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getCurrentUser();

    Response<UserDTO> getMyUserDetails();

    Response<UserDTO> getUserById(Long userId);

    Response<List<UserDTO>>getAllUsers();

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    Response<?> uploadProfilePicture(MultipartFile file);
}
