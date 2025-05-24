package com.example.api_springboot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api_springboot.modele.Client;
import com.example.api_springboot.modele.Commande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long>{
    long countByClient(Client client);
}
