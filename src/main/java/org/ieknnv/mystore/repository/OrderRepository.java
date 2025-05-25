package org.ieknnv.mystore.repository;

import java.util.List;

import org.ieknnv.mystore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT o
            FROM Order AS o
            WHERE o.user.id=:userId
            ORDER BY o.id DESC
            """)
    List<Order> findAllByUserId(long userId);
}
