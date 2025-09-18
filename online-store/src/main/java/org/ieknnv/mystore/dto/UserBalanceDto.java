package org.ieknnv.mystore.dto;

import lombok.*;
import org.ieknnv.mystore.enums.PaymentServiceError;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceDto {
    private Long userId;
    private BigDecimal amount;
    private PaymentServiceError error;
}
