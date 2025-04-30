package com.example.api_springboot.modele;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private int prix;
    private String photo;
    private boolean disponible;

    @ManyToOne
    @JoinColumn(name = "Vendeur", referencedColumnName = "id", nullable = false)
    private Vendeur vendeur;

}
