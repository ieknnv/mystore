package org.ieknnv.mystore.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@Table(name = "order_items")
public class OrderItem {

    @Id
    private long id;

    @Column("order_id")
    private long orderId;

    @Column("item_id")
    private long itemId;

    @Column("quantity")
    private long quantity;

    @Column("price")
    private BigDecimal price;
}
