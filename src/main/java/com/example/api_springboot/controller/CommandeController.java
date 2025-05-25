package com.example.api_springboot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> commandeDetail(@PathVariable Long id) {
        try {
            CommandeRequest detail = commandeService.commandeDetail(id);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return handleCommandeException(e);
        }
    }

    @PatchMapping("/{id}/en-livraison")
    public ResponseEntity<?> updateOrderToShipping(@PathVariable Long id) {
        try {
            CommandeStatus updatedOrder = commandeService.updateCommandeStatus(id, StatutCommande.En_livraison);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return handleCommandeException(e);
        }
    }

    // @PatchMapping("/{id}/complete")
    // public ResponseEntity<?> updateOrderToCompleted(@PathVariable Long id) {
    //     try {
    //         CommandeStatus updatedOrder = commandeService.updateCommandeStatus(id, StatutCommande.Terminé);
    //         return ResponseEntity.ok(updatedOrder);
    //     } catch (RuntimeException e) {
    //         return handleCommandeException(e);
    //     }
    // }

    @GetMapping
    public ResponseEntity<?> getAllVendorOrders() {
        try {
            List<CommandeRequest> vendorOrders = commandeService.getAllVendeurCommandes();
            return ResponseEntity.ok(vendorOrders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private ResponseEntity<?> handleCommandeException(RuntimeException e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage.contains("not authenticated") || errorMessage.contains("non authentifié")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentification requise", errorMessage));
        } else if (errorMessage.contains("not found") || errorMessage.contains("non trouvé")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Ressource introuvable", errorMessage));
        } else if (errorMessage.contains("Unauthorized access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Accès refusé", errorMessage));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erreur serveur", errorMessage));
    }

    // Simple error response DTO
    public record ErrorResponse(String error, String message) {}
    
}
