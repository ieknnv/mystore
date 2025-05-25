package org.ieknnv.mystore.repository;

import java.util.Optional;

import org.ieknnv.mystore.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("""
            SELECT c
            FROM Cart AS c
            WHERE c.user.id=:userId
            """)
    Optional<Cart> findByUserId(long userId);
}
