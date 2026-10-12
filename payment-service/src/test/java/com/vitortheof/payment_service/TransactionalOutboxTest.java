package com.vitortheof.payment_service;

import com.vitortheof.payment_service.config.RabbitConfig;
import com.vitortheof.payment_service.outbox.OutboxEvent;
import com.vitortheof.payment_service.outbox.OutboxEventRepository;
import com.vitortheof.payment_service.outbox.OutboxPublisher;
import com.vitortheof.payment_service.outbox.StatusOutbox;
import com.vitortheof.payment_service.repository.PagamentoRepository;
import com.vitortheof.payment_service.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

@SpringBootTest(properties = "outbox.agendamento.habilitado=false")
@Import(TestcontainersConfiguration.class)
class TransactionalOutboxTest {

    @Autowired PagamentoService pagamentoService;
    @Autowired PagamentoRepository pagamentoRepository;
    @Autowired OutboxPublisher publisher;
    @Autowired AmqpAdmin amqpAdmin;

    @MockitoSpyBean OutboxEventRepository outboxRepository;
    @MockitoSpyBean RabbitTemplate rabbitTemplate;

    @BeforeEach
    void limparBanco() {
        outboxRepository.deleteAll();
        pagamentoRepository.deleteAll();
    }

    @Test
    void naoDeveSalvarPagamentoQuandoGravacaoDoEventoFalha() {
        doThrow(new RuntimeException("falha simulada"))
                .when(outboxRepository).save(any());

        assertThatThrownBy(() ->
                pagamentoService.aprovar(UUID.randomUUID(), new BigDecimal("150.00")))
                .hasMessage("falha simulada");

        assertThat(pagamentoRepository.count()).isZero();
        assertThat(outboxRepository.count()).isZero();
    }

    @Test
    void deveManterEventoPendenteQuandoBrokerEstaForaEPublicarQuandoVoltar() {
        pagamentoService.aprovar(UUID.randomUUID(), new BigDecimal("150.00"));

        doThrow(new AmqpConnectException(new ConnectException("broker fora do ar")))
                .when(rabbitTemplate)
                .send(anyString(), anyString(), any(Message.class), any(CorrelationData.class));

        publisher.publicarPendentes();
        assertThat(unicoEvento().getStatus()).isEqualTo(StatusOutbox.PENDENTE);

        reset(rabbitTemplate); // o broker "volta"

        publisher.publicarPendentes();
        assertThat(unicoEvento().getStatus()).isEqualTo(StatusOutbox.PUBLICADO);
    }

    @Test
    void devePublicarEventoComIdDaOutboxComoMessageId() {
        Queue filaDeTeste = new Queue("teste.pagamento-aprovado", false, false, true);
        TopicExchange exchange = new TopicExchange(RabbitConfig.EXCHANGE);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(filaDeTeste);
        amqpAdmin.declareBinding(BindingBuilder.bind(filaDeTeste).to(exchange).with(RabbitConfig.ROUTING_KEY));

        pagamentoService.aprovar(UUID.randomUUID(), new BigDecimal("150.00"));
        publisher.publicarPendentes();

        Message recebida = rabbitTemplate.receive(filaDeTeste.getName(), 5000);
        OutboxEvent evento = unicoEvento();

        assertThat(recebida).isNotNull();
        assertThat(recebida.getMessageProperties().getMessageId())
                .isEqualTo(evento.getId().toString());
        assertThat(evento.getStatus()).isEqualTo(StatusOutbox.PUBLICADO);
    }

    private OutboxEvent unicoEvento() {
        var eventos = outboxRepository.findAll();
        assertThat(eventos).hasSize(1);
        return eventos.getFirst();
    }
}