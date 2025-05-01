package com.example.api_springboot.repository;
import com.example.api_springboot.modele.Vendeur;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendeurRepository extends JpaRepository<Vendeur, Long> {
    Optional<Vendeur> findByEmail(String email);
}
