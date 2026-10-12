package com.vitortheof.orderservice;

import com.vitortheof.orderservice.domain.Pedido;
import com.vitortheof.orderservice.domain.StatusPedido;
import com.vitortheof.orderservice.event.PagamentoAprovadoEvent;
import com.vitortheof.orderservice.messaging.EventoProcessadoRepository;
import com.vitortheof.orderservice.repository.PedidoRepository;
import com.vitortheof.orderservice.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class IdempotenciaTest {

    @Autowired PedidoService pedidoService;
    @Autowired PedidoRepository pedidoRepository;
    @Autowired EventoProcessadoRepository eventoProcessadoRepository;

    @BeforeEach
    void limparBanco() {
        eventoProcessadoRepository.deleteAll();
        pedidoRepository.deleteAll();
    }

    @Test
    void deveProcessarEventoDuplicadoApenasUmaVez() {
        Pedido pedido = pedidoRepository.save(Pedido.novo(new BigDecimal("150.00")));
        var evento = eventoPara(pedido.getId());

        pedidoService.confirmarPagamento(evento);
        LocalDateTime atualizadoNaPrimeira = buscar(pedido.getId()).getAtualizadoEm();

        pedidoService.confirmarPagamento(evento); // mesma entrega, de novo

        Pedido depois = buscar(pedido.getId());
        assertThat(depois.getStatus()).isEqualTo(StatusPedido.PAGO);
        assertThat(depois.getAtualizadoEm()).isEqualTo(atualizadoNaPrimeira);
        assertThat(eventoProcessadoRepository.count()).isEqualTo(1);
    }

    @Test
    void deveDescartarEventoDePedidoInexistenteSemRegistrarComoProcessado() {
        var evento = eventoPara(UUID.randomUUID());

        assertThatThrownBy(() -> pedidoService.confirmarPagamento(evento))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class);

        assertThat(eventoProcessadoRepository.existsById(evento.eventId())).isFalse();
    }

    private PagamentoAprovadoEvent eventoPara(UUID pedidoId) {
        return new PagamentoAprovadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), pedidoId,
                new BigDecimal("150.00"), LocalDateTime.now());
    }

    private Pedido buscar(UUID id) {
        return pedidoRepository.findById(id).orElseThrow();
    }
}