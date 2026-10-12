package com.vitortheof.orderservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {

    @Id
    private UUID id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public static Pedido novo(BigDecimal valor) {
        Pedido p = new Pedido();
        p.id = UUID.randomUUID();
        p.valor = valor;
        p.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        p.criadoEm = LocalDateTime.now();
        p.atualizadoEm = p.criadoEm;
        return p;
    }

    public void marcarComoPago() {
        this.status = StatusPedido.PAGO;
        this.atualizadoEm = LocalDateTime.now();
    }
}
