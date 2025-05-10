package com.example.api_springboot.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.api_springboot.modele.Vendeur;
import com.example.api_springboot.repository.VendeurRepository;
import com.example.api_springboot.security.JwtUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Service
public class VendeurService {
    private final VendeurRepository vendeurRepository;
    private final JwtUtil jwtUtil;
    private PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public VendeurService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                      VendeurRepository vendeurRepository, PasswordEncoder encoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.vendeurRepository = vendeurRepository;
    this.encoder = encoder;
}
    public Vendeur createVendeur(Vendeur vendeur, MultipartFile photoFile) throws IOException{
        if(photoFile!=null && !photoFile.isEmpty()){
            try {
                String uploadDir = "uploads/";
                String filename = UUID.randomUUID()+ " - "+photoFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + filename);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, photoFile.getBytes());

                vendeur.setPhoto(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement de la photo", e);
            }
        }
        vendeur.setPassword(encoder.encode(vendeur.getPassword()));
        return vendeurRepository.save(vendeur);
    }

    public String authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        // Si l'authentification est réussie, on génère un token
        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(email); // Ici tu pourrais aussi utiliser authentication.getName() si nécessaire
        } else {
            throw new RuntimeException("Authentification échouée");
        }
    }


    public Vendeur getVendeur(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            return vendeurRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    public Vendeur deleteVendeur(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendeur non trouvé"));
            
            vendeurRepository.delete(vendeur);
            return vendeur;
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    public Vendeur updateVendeur(Vendeur vend){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Vendeur existingvendeur =  vendeurRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));

            if(vend.getNom()!=null){
                existingvendeur.setNom(vend.getNom());
            }
            if(vend.getAdresse()!=null){
                existingvendeur.setAdresse(vend.getAdresse());
            }
            if(vend.getEmail()!=null){
                existingvendeur.setEmail(vend.getEmail());
            }
            if(vend.getPhone()!=0){
                existingvendeur.setPhone(vend.getPhone());
            }
            if(vend.getHoraire()!=null){
                existingvendeur.setHoraire(vend.getHoraire());
            }
            if(vend.getPhoto()!=null){
                existingvendeur.setPhoto(vend.getPhoto());
            }
            if(vend.getPassword()!=null){
                existingvendeur.setPassword(vend.getPassword());
            }

            return vendeurRepository.save(existingvendeur);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
}

