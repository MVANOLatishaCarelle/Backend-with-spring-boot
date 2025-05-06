package com.example.api_springboot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.api_springboot.modele.Plat;
import com.example.api_springboot.modele.Vendeur;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long>{
    <Optional>Plat findByVendeur(Vendeur vendeur);
}
