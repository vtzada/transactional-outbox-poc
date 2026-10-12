package com.vitortheof.orderservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarPedidoRequest(@NotNull @Positive BigDecimal valor) {
}
