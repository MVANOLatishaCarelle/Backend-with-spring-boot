package com.example.api_springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.api_springboot.dto.CommandeRequest;
import com.example.api_springboot.dto.CommandeStatus;
import com.example.api_springboot.modele.ArticleCommande;
import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.Commande;
import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.modele.StatutCommande;
import com.example.api_springboot.repository.ArticleCommandeRepository;
import com.example.api_springboot.repository.ClientRepository;
import com.example.api_springboot.repository.CommandeRepository;
import com.example.api_springboot.repository.PlatRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final PlatRepository platRepository;
    private final ArticleCommandeRepository articleCommandeRepository;

    @Transactional
    public Commande creerCommande(CommandeRequest request) {
        Client client = clientRepository.findById(request.getClientId())
            .orElseThrow(() -> new RuntimeException("Client introuvable"));

        // Create the order
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setPrixTotal(request.getPrixTotal());
        commande.setStatut(StatutCommande.En_attente);
        
        commande = commandeRepository.save(commande);

        // Add order items
        for (CommandeRequest.ArticleRequest articleReq : request.getArticles()) {
            Plat plat = platRepository.findById(articleReq.getPlatId())
                .orElseThrow(() -> new RuntimeException("Plat non trouvé: " + articleReq.getPlatId()));

            ArticleCommande article = new ArticleCommande();
            article.setPlat(plat);
            article.setQuantite(articleReq.getQuantite());
            article.setPrix(articleReq.getPrix());
            article.setCommande(commande);
            
            articleCommandeRepository.save(article);
        }

        return commande;
    }

    // Custom exception
    public class OrderLimitException extends RuntimeException {
        public OrderLimitException(String message) {
            super(message);
        }
    }

    @Transactional(readOnly = true)
    public CommandeRequest commandeDetail(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvé avec cet Id: " + id));

        return mapToCommandeDetailDTO(commande);
    }

    @Transactional
    public CommandeStatus updateCommandeStatus(Long id, StatutCommande newStatus) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvé avec cet Id: " + id));

        commande.setStatut(newStatus);
        Commande updatedCommande = commandeRepository.save(commande);

        return new CommandeStatus(
                updatedCommande.getId(),
                updatedCommande.getStatut()
        );
    }

    private CommandeRequest mapToCommandeDetailDTO(Commande commande) {
        return new CommandeRequest(
            commande.getClient(),
            commande.getPrixTotal(),
            commande.getStatut(),
            commande.getRating(),
            commande.getCommentaire(),
            mapToArticleRequests(commande.getArticleCommandes())
        );
    }

    private List<CommandeRequest.ArticleRequest> mapToArticleRequests(List<ArticleCommande> articles) {
    return articles.stream()
            .map(article -> new CommandeRequest.ArticleRequest(
                    article.getPlat().getId(),
                    article.getQuantite(),
                    article.getPrix()
            ))
            .toList();
}
}
