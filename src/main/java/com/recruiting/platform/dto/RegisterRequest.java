package com.recruiting.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private String firstName;
    private String lastName;

    // Optional: client can provide role like "ADMIN", "RECRUITER", "CANDIDATE" or full enum name
    private String role;

    // For compatibility with the example in the request (optional)
    private String username;

    public String getEmail() {
        // If email is not provided but username is, use username as email
        if ((email == null || email.isEmpty()) && username != null && !username.isEmpty()) {
            return username;
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        // If firstName is not provided, use username
        if (firstName == null || firstName.isEmpty()) {
            return getUsername();
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        // If lastName is not provided, use username
        if (lastName == null || lastName.isEmpty()) {
            return getUsername();
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}