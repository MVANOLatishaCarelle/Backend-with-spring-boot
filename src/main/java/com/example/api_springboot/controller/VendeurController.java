package com.example.api_springboot.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

}
