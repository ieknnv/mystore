package org.ieknnv.mystore.dto;

import lombok.Builder;
import lombok.Getter;
import org.ieknnv.mystore.enums.PaymentServiceError;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class CartPageDto {
    private List<ItemDto> itemDtoList;
    private BigDecimal total;
    private boolean cartEmpty;
    private boolean userBalanceAvailable;
    private BigDecimal userBalance;
    private boolean enablePayment;
    private PaymentServiceError paymentError;
}
