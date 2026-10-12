package com.vitortheof.orderservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoAprovadoEvent(
        UUID eventId,
        UUID pagamentoId,
        UUID pedidoId,
        BigDecimal valor,
        LocalDateTime aprovadoEm
) {}
