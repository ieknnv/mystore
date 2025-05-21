package org.ieknnv.mystore.service;

import java.util.Map;

import org.ieknnv.mystore.entity.Item;
import org.ieknnv.mystore.enums.CartAction;

public interface CartService {

    void updateCart(long userId, long itemId, CartAction cartAction);

    Map<Item, Long> getCartItemsForUser(long userId);
}
