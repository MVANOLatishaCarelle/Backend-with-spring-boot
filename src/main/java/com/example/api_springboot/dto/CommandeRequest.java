package com.example.api_springboot.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.StatutCommande;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeRequest {
    private int prixTotal;
    private StatutCommande statut;
    private int rating;
    private String commentaire;
    private List<ArticleRequest> articles = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleRequest {
        private Long platId;
        private int quantite;
        private int prix;
    }
}

