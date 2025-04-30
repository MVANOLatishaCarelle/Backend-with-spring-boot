package com.example.api_springboot.modele;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String adresse;
    private int phone;
    private String password;
    private String heureOuverture;
    private String photo;

}
