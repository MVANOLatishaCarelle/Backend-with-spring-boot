package com.example.api_springboot.modele;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int prixTotal;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(nullable = true)
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut = StatutCommande.En_attente;
    
    @ManyToOne
    @JoinColumn(name ="client_id", referencedColumnName = "id")
    private Client client;

    // Add this inverse relationship
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleCommande> articleCommandes = new ArrayList<>();

}
