package com.vitortheof.orderservice.service;

import com.vitortheof.orderservice.domain.Pedido;
import com.vitortheof.orderservice.event.PagamentoAprovadoEvent;
import com.vitortheof.orderservice.messaging.EventoProcessado;
import com.vitortheof.orderservice.messaging.EventoProcessadoRepository;
import com.vitortheof.orderservice.repository.PedidoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final EventoProcessadoRepository eventoProcessadoRepository;

    @Transactional
    public void confirmarPagamento(PagamentoAprovadoEvent evento) {
        //verificar se o evento ja foi processado
        if (eventoProcessadoRepository.existsById(evento.eventId())) {
            log.info("Evento {} já processado, ignorando", evento.eventId());
            return;
        }

        //procurar no banco o pedido informado na msg
        Pedido pedido = pedidoRepository.findById(evento.pedidoId())
                //se n encontrar sinaliza ao listener q deve rejeitar a msg e coloca novamente na fila original
                .orElseThrow(() -> new AmqpRejectAndDontRequeueException(
                        "Pedido " + evento.pedidoId() + " não encontrado"));

        pedido.marcarComoPago();
        eventoProcessadoRepository.save(new EventoProcessado(evento.eventId()));

        log.info("Pedido {} marcado como PAGO (evento {})", pedido.getId(), evento.eventId());
    }
}