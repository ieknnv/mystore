package org.ieknnv.mystore.enums;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortOrder {
    NO(Sort.unsorted()),
    ALPHA(Sort.by("name").ascending()),
    PRICE(Sort.by("price").ascending());

    @Getter
    private final Sort sort;
}
