package com.example.api_springboot.controller;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api_springboot.dto.AuthRequest;
import com.example.api_springboot.modele.Client;
import com.example.api_springboot.service.ClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client){
        Client newClient = clientService.createClient(client);
        return ResponseEntity.ok(newClient);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@RequestBody AuthRequest request){
        try {
            String token = clientService.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public Client getClient(){
        Client client = clientService.getClient();
        return client;
    }

    @DeleteMapping
    public ResponseEntity<Client> deleteClient(){
        Client client = clientService.deleteClient();
        return ResponseEntity.ok(client);
    } 

    @PatchMapping
    public ResponseEntity<Client> updateClient(@RequestBody Client client){
        Client cl = clientService.updateClient(client);
        return ResponseEntity.ok(cl);
    }    
}
