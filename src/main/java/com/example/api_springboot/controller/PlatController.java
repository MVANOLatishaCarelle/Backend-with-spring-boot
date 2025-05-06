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

import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.service.PlatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plat")
@RequiredArgsConstructor
public class PlatController {
    private final PlatService platService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Plat> createPlat(@RequestPart("plat") @Valid Plat plat, @RequestPart("photo") MultipartFile photoFile) throws IOException{
        Plat newPlat = platService.createPlat(plat, photoFile);
        return ResponseEntity.ok(newPlat);
    }

    @GetMapping
    public Plat getPlat(){
        Plat plat = platService.getPlat();
        return plat;
    }

    @DeleteMapping
    public ResponseEntity<Plat> deletePlat(){
        Plat plat = platService.deletePlat();
        return ResponseEntity.ok(plat);
    }

    @PatchMapping
    public ResponseEntity<Plat> updatePlat(@RequestBody Plat plat){
        Plat cl = platService.updatePlat(plat);
        return ResponseEntity.ok(cl);
    }

}
