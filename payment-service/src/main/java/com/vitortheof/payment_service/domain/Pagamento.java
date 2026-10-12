package com.vitortheof.payment_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    private UUID id;

    @Column(name = "pedido_id", nullable = false)
    private UUID pedidoId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public static Pagamento aprovado(UUID pedidoId, BigDecimal valor) {
        Pagamento p = new Pagamento();
        p.id = UUID.randomUUID();
        p.pedidoId = pedidoId;
        p.valor = valor;
        p.status = StatusPagamento.APROVADO;
        p.criadoEm = LocalDateTime.now();
        return p;
    }

}
