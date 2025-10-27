package org.ieknnv.mystore.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentServiceError {
    NOT_ENOUGH_MONEY("К сожалению, средств недостаточно для оплаты заказа."),
    SERVICE_UNAVAILABLE("К сожалению, сервис платежей в настоящее время недоступен. Попробуйте позже."),
    UNEXPECTED_ERROR("Произошла неизвестная ошибка при обращении к сервису платежей.");

    private final String message;
}
