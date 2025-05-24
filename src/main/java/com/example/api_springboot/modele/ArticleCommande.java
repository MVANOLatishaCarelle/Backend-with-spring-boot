package com.example.api_springboot.modele;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantite;
    private int prix;

    @ManyToOne
    @JoinColumn(name = "plat_id", referencedColumnName = "id", nullable = false)
    private Plat plat;

    @ManyToOne
    @JoinColumn(name = "commande_id", referencedColumnName = "id", nullable = false)
    private Commande commande;
}
