package com.example.api_springboot.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.modele.Vendeur;
import com.example.api_springboot.repository.PlatRepository;
import com.example.api_springboot.repository.VendeurRepository;

// import java.io.IOException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatService {
    private final VendeurRepository vendeurRepository;
    private final PlatRepository platRepository;

    public Plat createPlat(Plat plat, MultipartFile photoFile) throws IOException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));

            if(photoFile!=null && !photoFile.isEmpty()){
                try {
                    String uploadDir = "uploads/";
                    String filename = UUID.randomUUID()+" - "+photoFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + filename);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, photoFile.getBytes());

                    plat.setPhoto(filePath.toString());
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors de l'enregistrement de la photo", e);
                }
            }
            plat.setVendeur(vendeur);
            return platRepository.save(plat);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    public List<Plat> getPlatsDuVendeurConnecte(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication ==null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            throw new RuntimeException("Utilisateur non authentifié");
        }
            
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));
            
            return platRepository.findByVendeur(vendeur);        
    }

    public List<Plat> getPlatByNomPourVendeurConnecte(String nom){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication ==null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            throw new RuntimeException("Utilisateur non authentifié");
        }
            
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));

            List<Plat> plats = platRepository.findByNomAndVendeur(nom, vendeur);
            
            if(plats!=null){
                throw new RuntimeException("Aucun plat trouvé avec ce nom pour ce vendeu");
            }
            return plats;
    }

    public List<Plat> getPlatByNom(String nom){
        List<Plat> plats = platRepository.findByNom(nom);

        if(plats!=null){
            throw new RuntimeException("Aucun plat trouvé");
        }
        return plats;
    }

    public List<Plat> getPlatByDisponibilite(boolean disponible){
        List<Plat> plats = platRepository.findByDisponibilite(disponible);

        if(plats!=null){
            throw new RuntimeException("Aucun plat trouvé");
        }
        return plats;
    }

    public Plat getPlatByPopularity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));
            
            return platRepository.getAll(disponible);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
    
    public Plat deletePlat(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendeur non trouvé"));
            
            platRepository.delete(plat);
            return plat;
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
    
    public Plat updatePlat(Plat plat){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Vendeur vendeur = vendeurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendeur non trouvé"));
            
            if(plat.getNom()!=null){
                p.setNom(plat.getNom());
            }
            if(plat.getDescription()!=null){
                p.setDescription(plat.getDescription());
            }
            if(plat.getPrix() != 0){
                p.setPrix(plat.getPrix());
            }
            if(plat.getPhoto()!=null){
                p.sePhoto(plat.getPhoto());
            }
            if(plat.getDisponible()!=true){
                p.setDisponible(plat.getDisponible());
            }

            return platRepository.save(p);
            
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
}
