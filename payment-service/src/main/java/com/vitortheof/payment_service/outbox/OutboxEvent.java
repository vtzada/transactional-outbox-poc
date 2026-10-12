package com.vitortheof.payment_service.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId; //id do pagamento ao qual o evento se refere para ajudar a rastrear e manter a ordem por entidade

    @Column(nullable = false)
    private String tipo; // o nome do evento (pagamento aprovado). o worker usar para decidir o routing key

    @Column(nullable = false)
    private String payload; // evento em json, pronto pro envio. o worker nao precisa consultar mais nd

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOutbox status;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;

    public static OutboxEvent pendente(UUID id, UUID aggregateId, String tipo, String payload) {
        OutboxEvent e = new OutboxEvent();
        e.id = id;
        e.aggregateId = aggregateId;
        e.tipo = tipo;
        e.payload = payload;
        e.status = StatusOutbox.PENDENTE;
        e.criadoEm = LocalDateTime.now();
        return e;
    }

    public void marcarComoPublicado() {
        this.status = StatusOutbox.PUBLICADO;
        this.publicadoEm = LocalDateTime.now();
    }

}
