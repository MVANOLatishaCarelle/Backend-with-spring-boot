package com.example.api_springboot.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.api_springboot.modele.Client;
import com.example.api_springboot.repository.ClientRepository;
import com.example.api_springboot.security.JwtUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Client createClient(Client client){
        if(clientRepository.existsByEmail(client.getEmail())){
            throw new IllegalArgumentException("Un client avec cet email existe déjà");
        }
        client.setPassword(encoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public String authenticate(Client client){
        Client cl = clientRepository.findByEmail(client.getEmail()).orElseThrow(()-> new RuntimeException("Email non trouve"));

        if(encoder.matches(client.getPassword(), cl.getPassword())){
            return jwtUtil.generateToken(cl.getEmail());
        }else{
            throw new RuntimeException("Nom d'utilisateur ou Mot de passe incorrect");
        }
    }

    public Client getClient(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            return clientRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    public Client deleteClient(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Client client = clientRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Client non trouvé"));

            clientRepository.delete(client);
            return client;
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    public Client updateClient(Client cl){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
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
            if(cl.getPhone()!=0){
                existingClient.setPhone(cl.getPhone());
            }
            if(cl.getPassword()!=null){
                existingClient.setPassword(cl.getPassword());
            }

            return clientRepository.save(existingClient);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
}
