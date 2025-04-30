package com.example.api_springboot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.api_springboot.modele.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
}
