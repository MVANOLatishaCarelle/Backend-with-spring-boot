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
    List<Plat> findByDisponible(boolean disponible);

    @Query("SELECT p FROM Plat p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :motcle, '%')) AND p.vendeur = :vendeur")
    List<Plat> findByNomIgnoreCaseAndVendeur(@Param("motcle") String motcle, @Param("vendeur") Vendeur vendeur);

    @Query("SELECT ac.plat FROM ArticleCommande ac WHERE ac.plat.vendeur = :vendeur GROUP BY ac.plat ORDER BY SUM(ac.quantite)")
    List<Plat> findPlatsByVendeurOrderByPopularite(@Param("vendeur") Vendeur vendeur);

    @Query("SELECT ac.plat FROM ArticleCommande ac GROUP BY ac.plat ORDER BY SUM(ac.quantite) DESC")
    List<Plat> findPlatsPopulaires();
}
