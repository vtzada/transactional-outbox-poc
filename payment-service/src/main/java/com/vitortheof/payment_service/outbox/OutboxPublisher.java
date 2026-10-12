package com.vitortheof.payment_service.outbox;

import com.vitortheof.payment_service.config.RabbitConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.core.NestedExceptionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private static final int TAMANHO_LOTE = 50;

    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @Scheduled(fixedDelay = 2000)
    public void publicarPendentes() {
        List<OutboxEvent> pendentes = repository.buscarPendentesParaPublicar(TAMANHO_LOTE);

        for (OutboxEvent evento : pendentes ) {
            try {
                publicar(evento);
                evento.marcarComoPublicado();
                log.info("Evento {} publicado", evento.getId());
            } catch (Exception e) {
                log.warn("Falha ao publicar o evento {}: {}. Nova tentativa no próximo ciclo.",
                        evento.getId(), NestedExceptionUtils.getMostSpecificCause(e).toString());
                break;
            }
        }
    }

    private void publicar(OutboxEvent evento) throws Exception {
        Message messagem = MessageBuilder
                .withBody(evento.getPayload().getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setMessageId(evento.getId().toString())
                .build();

        CorrelationData correlation = new CorrelationData(evento.getId().toString());
        rabbitTemplate.send(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, messagem, correlation);

        var confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);
        if(!confirm.ack()) {
            throw new IllegalStateException("Broker recusou a mensagem: " + confirm.reason());
        }
    }
}
