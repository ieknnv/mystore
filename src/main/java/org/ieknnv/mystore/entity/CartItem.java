package org.ieknnv.mystore.entity;

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

@Table(name = "cart_items")
public class CartItem {

    @Id
    private long id;

    @Column("cart_id")
    private long cartId;

    @Column("item_id")
    private long itemId;

    @Column("quantity")
    private long quantity;
}
