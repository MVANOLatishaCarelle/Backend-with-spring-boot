package com.example.api_springboot.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.api_springboot.modele.Client;
import com.example.api_springboot.repository.ClientRepository;
import com.example.api_springboot.security.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;
    private PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public ClientService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                      ClientRepository clientRepository, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.clientRepository = clientRepository;
        this.encoder = encoder;
    }

    public Client createClient(Client client){
        if(clientRepository.existsByEmail(client.getEmail())){
            throw new IllegalArgumentException("Un client avec cet email existe déjà");
        }
        client.setPassword(encoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public String authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(email);
        } else {
            throw new RuntimeException("Authentification échouée");
        }
    }

    public Client getClient(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication ==null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            throw new RuntimeException("Utilisateur non authentifié");
        }
            String email = authentication.getName();
            return clientRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));
    }

    public Client deleteClient(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication ==null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            throw new RuntimeException("Utilisateur non authentifié");
        }
            String email = authentication.getName();
            Client client = clientRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Client non trouvé"));

            clientRepository.delete(client);
            return client;
    }

    public Client updateClient(Client cl){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication ==null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            throw new RuntimeException("Utilisateur non authentifié");
        }
            String email = authentication.getName();
            Client existingClient =  clientRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));

            if(cl.getNom()!=null){
                existingClient.setNom(cl.getNom());
            }
            if(cl.getPrenom()!=null){
                existingClient.setPrenom(cl.getPrenom());
            }
            if(cl.getEmail()!=null){
                existingClient.setEmail(cl.getEmail());
            }
            if(cl.getPhone()!=null){
                existingClient.setPhone(cl.getPhone());
            }
            if(cl.getPassword()!=null){
                existingClient.setPassword(cl.getPassword());
            }

            return clientRepository.save(existingClient);
    }
}
