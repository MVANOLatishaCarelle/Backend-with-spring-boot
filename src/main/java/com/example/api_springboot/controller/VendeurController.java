package com.example.api_springboot.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.api_springboot.modele.Vendeur;
import com.example.api_springboot.service.VendeurService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vendeur")
@RequiredArgsConstructor
public class VendeurController {
    private final VendeurService vendeurService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Vendeur> createVendeur(@RequestPart("vendeur") @Valid Vendeur vendeur, @RequestPart("photo") MultipartFile photoFile) throws IOException{
        Vendeur newVendeur = vendeurService.createVendeur(vendeur, photoFile);
        return ResponseEntity.ok(newVendeur);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@RequestBody Vendeur vendeur){
        try {
            String token = vendeurService.authenticate(vendeur);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public Vendeur getVendeur(){
        Vendeur vendeur = vendeurService.getVendeur();
        return vendeur;
    }

    @DeleteMapping
    public ResponseEntity<Vendeur> deleteVendeur(){
        Vendeur vendeur = vendeurService.deleteVendeur();
        return ResponseEntity.ok(vendeur);
    }

    @PatchMapping
    public ResponseEntity<Vendeur> updateVendeur(@RequestBody Vendeur vendeur){
        Vendeur cl = vendeurService.updatevendeur(vendeur);
        return ResponseEntity.ok(cl);
    }

}
