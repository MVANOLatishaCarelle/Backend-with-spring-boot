package com.example.api_springboot.security;

import com.example.api_springboot.modele.Vendeur;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class VendeurDetails implements UserDetails {
    private final Vendeur vendeur;

    public VendeurDetails(Vendeur vendeur) {
        this.vendeur = vendeur;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    @Override
    public String getUsername() {
        return vendeur.getEmail();
    }

    @Override
    public String getPassword() {
        return vendeur.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Aucun rôle, c’est bien ce que tu veux
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
