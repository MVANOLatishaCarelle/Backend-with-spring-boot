package com.example.api_springboot.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.api_springboot.dto.CommandeRequest;
import com.example.api_springboot.dto.CommandeStatus;
import com.example.api_springboot.modele.ArticleCommande;
import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.Commande;
import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.modele.StatutCommande;
import com.example.api_springboot.modele.Vendeur;
import com.example.api_springboot.repository.ArticleCommandeRepository;
import com.example.api_springboot.repository.ClientRepository;
import com.example.api_springboot.repository.CommandeRepository;
import com.example.api_springboot.repository.PlatRepository;
import com.example.api_springboot.repository.VendeurRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final PlatRepository platRepository;
    private final ArticleCommandeRepository articleCommandeRepository;
    private final VendeurRepository vendeurRepository;

    @Transactional
    public Commande creerCommande(CommandeRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request cannot be null");
        }
        if (request.getArticles() == null || request.getArticles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one article is required");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            Client client = clientRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Email non trouvé"));

            System.out.println("Client email from token: " + authentication.getName());
            // Create the order
            Commande commande = new Commande();
            commande.setClient(client);
            commande.setPrixTotal(request.getPrixTotal());
            commande.setStatut(StatutCommande.En_attente);

            if (request.getRating() != 0) {
                commande.setRating(request.getRating());
            }
            if (request.getCommentaire() != null) {
                commande.setCommentaire(request.getCommentaire());
            }
            // Save the commande first (optional if using CascadeType.ALL)
            commande = commandeRepository.save(commande);

            // Add order items
            for (CommandeRequest.ArticleRequest articleReq : request.getArticles()) {
                if (articleReq.getPlatId() == null) {
                    throw new IllegalArgumentException("Plat ID is required");
                }

                Plat plat = platRepository.findById(articleReq.getPlatId())
                    .orElseThrow(() -> new RuntimeException("Plat non trouvé: " + articleReq.getPlatId()));

                ArticleCommande article = new ArticleCommande();
                article.setPlat(plat);
                article.setQuantite(articleReq.getQuantite());
                article.setPrix(articleReq.getPrix());
                article.setCommande(commande);

                // Either save manually or let JPA cascade handle it
                articleCommandeRepository.save(article); // Option 1 (manual)
                // commande.getArticleCommandes().add(article); // Option 2 (auto-cascade)
            }

            return commandeRepository.save(commande); // Re-save to update any changes
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }


    @Transactional(readOnly = true)
    public CommandeRequest commandeDetail(Long id) {
        // Get authenticated vendor from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Vendor not authenticated");
        }
        
        String email = authentication.getName();
        Vendeur vendeur = vendeurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        // Find the order
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvé avec cet Id: " + id));
        
        // Check if any dish in the order belongs to this vendor
        boolean isOrderRelatedToVendor = commande.getArticleCommandes().stream()
                .anyMatch(article -> article.getPlat().getVendeur().getId().equals(vendeur.getId()));
        
        if (!isOrderRelatedToVendor) {
            throw new RuntimeException("Unauthorized access - this order doesn't belong to your vendor account");
        }
        
        return mapToCommandeDetailDTO(commande);
    }

    @Transactional
    public CommandeStatus updateCommandeStatus(Long id, StatutCommande newStatus) {
        // Get authenticated vendor from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Vendor not authenticated");
        }
        
        String email = authentication.getName();
        Vendeur vendeur = vendeurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        // Find the order
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvé avec cet Id: " + id));
        
        // Check if any dish in the order belongs to this vendor
        boolean isOrderRelatedToVendor = commande.getArticleCommandes().stream()
                .anyMatch(article -> article.getPlat().getVendeur().getId().equals(vendeur.getId()));
        
        if (!isOrderRelatedToVendor) {
            throw new RuntimeException("Unauthorized access - this order doesn't belong to your vendor account");
        }
        
        commande.setStatut(newStatus);
        if (shouldGenerateQrCode(commande.getStatut(), newStatus)) {
            String qrCodeData = generateQrCodeData(commande);
            commande.setQrCodeData(qrCodeData);
        }
        Commande updatedCommande = commandeRepository.save(commande);
        
        return new CommandeStatus(
                updatedCommande.getId(),
                updatedCommande.getStatut(),
                updatedCommande.getQrCodeData()
        );
    }

    private boolean shouldGenerateQrCode(StatutCommande oldStatus, StatutCommande newStatus) {
        // Generate QR code only when order moves to specific statuses
        return newStatus == StatutCommande.En_livraison;
    }

    private String generateQrCodeData(Commande commande) {
        // Create a unique payload containing order info
        Map<String, Object> qrPayload = new HashMap<>();
        qrPayload.put("orderId", commande.getId());
        qrPayload.put("clientId", commande.getClient().getId());
        qrPayload.put("status", commande.getStatut().name());
        qrPayload.put("timestamp", Instant.now().toString());
        qrPayload.put("total", commande.getPrixTotal());
        
        // Add security hash
        String securityHash = generateSecurityHash(commande);
        qrPayload.put("hash", securityHash);
        
        try {
            return new ObjectMapper().writeValueAsString(qrPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate QR code data", e);
        }
    }

    private String generateSecurityHash(Commande commande) {
        // Create a simple hash for basic verification
        String raw = commande.getId() + "|" + 
                    commande.getClient().getId() + "|" + 
                    commande.getStatut().name() + "|" +
                    "your-secret-salt"; // Use a proper secret from configuration
        
        return DigestUtils.sha256Hex(raw);
    }

    private CommandeRequest mapToCommandeDetailDTO(Commande commande) {
        return new CommandeRequest(
            commande.getId(),
            commande.getPrixTotal(),
            commande.getStatut(),
            commande.getRating() != null ? commande.getRating() : 0,
            commande.getCommentaire(),
            commande.getClient().getId(),
            commande.getClient().getPhone(),
            commande.getClient().getEmail(),
            mapToArticleRequests(commande.getArticleCommandes())
        );
    }

    private List<CommandeRequest.ArticleRequest> mapToArticleRequests(List<ArticleCommande> articles) {
        return articles.stream()
                .map(article -> new CommandeRequest.ArticleRequest(
                        article.getPlat().getId(),
                        article.getPlat().getPhoto(),
                        article.getPlat().getNom(),
                        article.getQuantite(),
                        article.getPrix()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommandeRequest> getAllVendeurCommandes() {
        // Get authenticated vendor from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Vendor not authenticated");
        }
        
        String email = authentication.getName();
        Vendeur vendeur = vendeurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        // Get all orders that contain at least one dish from this vendor
        List<Commande> allCommandes = commandeRepository.findAll();
        
        // Filter orders to only include those with dishes from this vendor
        List<Commande> vendorCommandes = allCommandes.stream()
                .filter(commande -> commande.getArticleCommandes().stream()
                        .anyMatch(article -> article.getPlat().getVendeur().getId().equals(vendeur.getId())))
                .collect(Collectors.toList());
        
        // Map to DTOs
        return vendorCommandes.stream()
                .map(this::mapToCommandeDetailDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommandeRequest> getAllVendeurCommandesSaufLivre() {
        // Get authenticated vendor from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Vendor not authenticated");
        }
        
        String email = authentication.getName();
        Vendeur vendeur = vendeurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        // Get all orders that contain at least one dish from this vendor and exclude "LIVRE" status
        List<Commande> vendorCommandes = commandeRepository.findAll().stream()
                .filter(commande -> 
                    // Check if order contains vendor's dishes
                    commande.getArticleCommandes().stream()
                        .anyMatch(article -> article.getPlat().getVendeur().getId().equals(vendeur.getId()))
                    &&
                    // Exclude "LIVRE" status orders
                    !commande.getStatut().equals(StatutCommande.Livre) // or "LIVRE" if using String
                )
                .collect(Collectors.toList());
        
        // Map to DTOs
        return vendorCommandes.stream()
                .map(this::mapToCommandeDetailDTO)
                .collect(Collectors.toList());
    }
  
}
