package com.example.api_springboot.dto;

import com.example.api_springboot.modele.StatutCommande;

public class CommandeStatus {
    private Long id;
    private StatutCommande statut;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public StatutCommande getStatut() {
        return statut;
    }
    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }
    public CommandeStatus(Long id, StatutCommande statut) {
        this.id = id;
        this.statut = statut;
    }
}
