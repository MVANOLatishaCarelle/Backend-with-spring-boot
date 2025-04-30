package com.example.api_springboot.modele;

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
    private int rating;
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut = StatutCommande.En_attente;
    
    @ManyToOne
    @JoinColumn(name ="Client", referencedColumnName = "id", nullable = false)
    private Client client;

}
