package com.example.api_springboot.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.Commande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long>{
    long countByClient(Client client);
    @Query("SELECT DISTINCT c FROM Commande c JOIN c.articleCommandes ac JOIN ac.plat p WHERE p.vendeur.id = :vendeurId")
    List<Commande> findByVendeurId(@Param("vendeurId") Long vendeurId);
    List<Commande> findByClient(Client client);

}
