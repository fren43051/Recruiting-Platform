package com.recruiting.platform.service;

import com.recruiting.platform.model.User;
import java.util.List;

public interface UserService {
    User findByEmail(String email);
    boolean adminExists();
    List<User> getAllUsers();
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
}
