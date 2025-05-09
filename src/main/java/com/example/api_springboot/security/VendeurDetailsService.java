package com.example.api_springboot.security;

import com.example.api_springboot.modele.Vendeur;
import com.example.api_springboot.repository.VendeurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VendeurDetailsService implements UserDetailsService {

    private final VendeurRepository vendeurRepository;

    public VendeurDetailsService(VendeurRepository vendeurRepository) {
        this.vendeurRepository = vendeurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Vendeur vendeur = vendeurRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Vendeur non trouvé"));
        return new VendeurDetails(vendeur);
    }
}
