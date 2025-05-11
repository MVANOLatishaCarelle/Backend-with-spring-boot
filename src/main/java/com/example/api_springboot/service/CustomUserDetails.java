package com.example.api_springboot.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.Vendeur;

public class CustomUserDetails implements UserDetails{
    private final Object user;

    public CustomUserDetails(Object user) {
        this.user = user;
    }

    public Object getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        if (user instanceof Vendeur) {
            return ((Vendeur) user).getEmail();
        } else if (user instanceof Client) {
            return ((Client) user).getEmail();
        }
        return null;
    }

    @Override
    public String getPassword() {
        if (user instanceof Vendeur) {
            return ((Vendeur) user).getPassword();
        } else if (user instanceof Client) {
            return ((Client) user).getPassword();
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
