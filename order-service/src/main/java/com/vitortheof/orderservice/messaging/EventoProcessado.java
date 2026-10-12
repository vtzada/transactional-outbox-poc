package com.vitortheof.orderservice.messaging;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos_processados")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventoProcessado {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "processado_em", nullable = false)
    private LocalDateTime processadoEm;

    public EventoProcessado(UUID eventId) {
        this.eventId = eventId;
        this.processadoEm = LocalDateTime.now();
    }
}