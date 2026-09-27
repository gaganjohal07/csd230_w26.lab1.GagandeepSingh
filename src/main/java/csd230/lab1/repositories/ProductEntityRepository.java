package csd230.lab1.repositories;

import csd230.lab1.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEntityRepository
        extends JpaRepository<ProductEntity, Long> {
}