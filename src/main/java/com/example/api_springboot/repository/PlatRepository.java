package com.example.api_springboot.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.modele.Vendeur;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long>{
    List<Plat> findByVendeur(Vendeur vendeur);
    List<Plat> findByNom(String nom);
    List<Plat> findByDisponibilite(boolean disponible);
    List<Plat> findByNomAndVendeur(String nom, Vendeur vendeur);

    @Query("SELECT ac.plat FROM article_commande WHERE ac.plat.vendeur = :vendeur GROUP BY ac.plat ORDER BY SUM(ac.quantite)")
    List<Plat> findPlatsByVendeurOrderByPopularite(@Param("vendeur") Vendeur vendeur);

    @Query("SELECT ac.plat FROM article_commande GROUP BY ac.plat ORDER BY SUM(ac.quantite)")
    List<Plat> findPlatsPopulaires();
}
