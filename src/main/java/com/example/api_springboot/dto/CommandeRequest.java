package com.example.api_springboot.dto;

import java.util.List;

import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.StatutCommande;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeRequest {
    private Long clientId;
    private int prixTotal;
    private StatutCommande statut;
    private int rating;
    private String commentaire;
    private List<ArticleRequest> articles;

    public CommandeRequest(Client client, int prixTotal, StatutCommande statut, 
                         int rating, String commentaire, List<ArticleRequest> articles) {
        this.clientId = client != null ? client.getId() : null;
        this.prixTotal = prixTotal;
        this.statut = statut;
        this.rating = rating;
        this.commentaire = commentaire;
        this.articles = articles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleRequest {
        private Long platId;
        private int quantite;
        private int prix;
    }
}

