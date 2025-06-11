package com.example.edu.school.auth.dto.response;

import com.example.edu.school.auth.model.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserCredentialResponse implements UserDetails {
    private String email;
    private String password;
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> this.getRole().getName());
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }
}
