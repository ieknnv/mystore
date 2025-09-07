package org.ieknnv.mystore.dto;

import java.math.BigDecimal;

import org.springframework.http.codec.multipart.FilePart;

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
public class NewItemDto {
    private String name;
    private String description;
    private FilePart image;
    private BigDecimal price;
}
