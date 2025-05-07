package com.example.api_springboot.controller;

import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.service.PlatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/plat")
@RequiredArgsConstructor
public class PlatController {

    private final PlatService platService;

    @PostMapping
    public ResponseEntity<Plat> createPlat(@RequestPart("plat") String platJson, @RequestPart("photo") MultipartFile photo) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Plat plat = objectMapper.readValue(platJson, Plat.class);
        Plat newPlat = platService.createPlat(plat, photo);
        return ResponseEntity.ok(newPlat);
    }

    @GetMapping("/mes-plats")
    public ResponseEntity<List<Plat>> getPlatsDuVendeurConnecte() {
        List<Plat> plats = platService.getPlatsDuVendeurConnecte();
        return ResponseEntity.ok(plats);
    }

    @GetMapping("/mes-plats/{nom}")
    public ResponseEntity<List<Plat>> getPlatByNomPourVendeurConnecte(@PathVariable String nom) {
        List<Plat> plats = platService.getPlatByNomPourVendeurConnecte(nom);
        return ResponseEntity.ok(plats);
    }

    @GetMapping("/{nom}")
    public ResponseEntity<List<Plat>> getPlatByNom(@PathVariable String nom) {
        List<Plat> plats = platService.getPlatByNom(nom);
        return ResponseEntity.ok(plats);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Plat>> getPlatByDisponibilite(@RequestParam boolean disponible) {
        List<Plat> plats = platService.getPlatByDisponibilite(disponible);
        return ResponseEntity.ok(plats);
    }

    @GetMapping("/mes-plats/populaires")
    public ResponseEntity<List<Plat>> getPlatsPopulairesDuVendeur() {
        List<Plat> plats = platService.getPlatsPopulairesDuVendeur();
        return ResponseEntity.ok(plats);
    }

    @GetMapping("/populaires")
    public ResponseEntity<List<Plat>> getPlatsPopulaires() {
        List<Plat> plats = platService.getPlatsPopulaires();
        return ResponseEntity.ok(plats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlat(@PathVariable Long id) {
        platService.deletePlat(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plat> updatePlat(@PathVariable Long id, @RequestPart("plat") String platJson, 
            @RequestPart(value="photo", required=false) MultipartFile photoFile) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        Plat plat = objectMapper.readValue(platJson, Plat.class);
        Plat updatePlat = platService.updatePlat(id, plat, photoFile);
        return ResponseEntity.ok(updatePlat);
    }
}
