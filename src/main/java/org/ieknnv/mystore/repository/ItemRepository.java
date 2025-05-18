package org.ieknnv.mystore.repository;

import org.ieknnv.mystore.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
