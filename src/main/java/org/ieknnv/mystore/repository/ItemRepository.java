package org.ieknnv.mystore.repository;

import java.util.Optional;

import org.ieknnv.mystore.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i.itemImage FROM Item i WHERE i.id = :id")
    Optional<byte[]> findItemImageById(@Param("id") Long id);

    @Query("""
    SELECT i
    FROM Item AS i
    WHERE LOWER(i.name) LIKE LOWER(CONCAT('%',:search,'%'))
       OR LOWER(i.description) LIKE LOWER(CONCAT('%',:search,'%'))
    """)
    Page<Item> findAllBySearchLine(String search, Pageable pageable);
}
