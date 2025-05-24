package com.example.api_springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api_springboot.dto.CommandeRequest;
import com.example.api_springboot.dto.CommandeStatus;
import com.example.api_springboot.modele.Commande;
import com.example.api_springboot.modele.StatutCommande;
import com.example.api_springboot.service.CommandeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/commande")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;
    
    @PostMapping
    public ResponseEntity<?> passerCommande(@RequestBody CommandeRequest request) {

        Commande nouvelleCommande = commandeService.creerCommande(request);
        return ResponseEntity.ok(nouvelleCommande);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeRequest> commandeDetail(@PathVariable Long id) {
        CommandeRequest detail = commandeService.commandeDetail(id);
        return ResponseEntity.ok(detail);
    } 

    @PatchMapping("/{id}")
    public ResponseEntity<CommandeStatus> updateOrderToShipping(@PathVariable Long id) {
        CommandeStatus updatedOrder = commandeService.updateCommandeStatus(id, StatutCommande.En_livraison);
        return ResponseEntity.ok(updatedOrder);
    }
    
}
