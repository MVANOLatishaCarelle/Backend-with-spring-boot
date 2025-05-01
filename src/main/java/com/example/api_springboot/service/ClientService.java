package com.example.api_springboot.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.api_springboot.modele.Client;
import com.example.api_springboot.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public Client createClient(Client client){
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

}
