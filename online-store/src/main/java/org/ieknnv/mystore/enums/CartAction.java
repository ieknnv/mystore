package org.ieknnv.mystore.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CartAction {
    PLUS("plus"),
    MINUS("minus"),
    DELETE("delete");

    @Getter
    private final String value;

    public static CartAction fromValue(String value) {
        for (CartAction action : CartAction.values()) {
            if (action.value.equalsIgnoreCase(value)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown cart action: " + value);
    }
}
