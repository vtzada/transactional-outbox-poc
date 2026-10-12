package com.vitortheof.payment_service.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record AprovarPagamentoRequest(
        @NotNull
        UUID pedidoId,
        @NotNull
        @Positive
        BigDecimal valor
) {

}
