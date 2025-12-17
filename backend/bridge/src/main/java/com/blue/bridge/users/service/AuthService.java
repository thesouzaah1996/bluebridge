package com.blue.bridge.users.service;

import com.blue.bridge.res.Response;
import com.blue.bridge.users.dto.LoginRequest;
import com.blue.bridge.users.dto.LoginResponse;
import com.blue.bridge.users.dto.RegistrationRequest;
import com.blue.bridge.users.dto.ResetPasswordRequest;

public interface AuthService {

    Response<String> register(RegistrationRequest request);

    Response<LoginResponse> login(LoginRequest loginRequest);

    Response<?> forgetPassword(String email);

    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
