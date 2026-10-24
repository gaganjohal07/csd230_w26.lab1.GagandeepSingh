
package csd230.lab1.repositories;

import csd230.lab1.entities.CartEntity;
import csd230.lab1.entities.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartEntityRepository
        extends JpaRepository<CartEntity, Long> {

    // Find shopping cart belonging to a specific user
    CartEntity findByUser(UserEntity user);

}
