package com.recruiting.platform.service;

import com.recruiting.platform.model.User;

public interface UserService {
    User findByEmail(String email);
    boolean adminExists();
}
