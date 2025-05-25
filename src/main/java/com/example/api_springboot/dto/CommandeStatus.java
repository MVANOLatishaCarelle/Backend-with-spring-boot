package com.example.api_springboot.dto;

import com.example.api_springboot.modele.StatutCommande;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeStatus {
    private Long id;
    private StatutCommande statut;
    private String qrCodeData;
}
