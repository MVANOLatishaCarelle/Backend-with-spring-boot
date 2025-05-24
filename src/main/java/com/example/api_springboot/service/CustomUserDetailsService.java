package com.example.api_springboot.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.Vendeur;
import com.example.api_springboot.repository.ClientRepository;
import com.example.api_springboot.repository.VendeurRepository;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final VendeurRepository vendeurRepository;
    private final ClientRepository clientRepository;

    public CustomUserDetailsService(VendeurRepository vendeurRepository, ClientRepository clientRepository){
        this.vendeurRepository = vendeurRepository;
        this.clientRepository = clientRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Vendeur vendeur = vendeurRepository.findByEmail(email).orElse(null);;
        if(vendeur != null){
            return new User(vendeur.getEmail(), vendeur.getPassword(), Collections.emptyList());
        }

        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client != null) {
            return new User(client.getEmail(), client.getPassword(), Collections.emptyList());
        }

        // Si aucun utilisateur n'est trouvé
        throw new UsernameNotFoundException("Utilisateur non trouvé");
    }
}
