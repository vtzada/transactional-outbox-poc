package com.vitortheof.payment_service.service;

import com.vitortheof.payment_service.domain.Pagamento;
import com.vitortheof.payment_service.event.PagamentoAprovadoEvent;
import com.vitortheof.payment_service.outbox.OutboxEvent;
import com.vitortheof.payment_service.outbox.OutboxEventRepository;
import com.vitortheof.payment_service.outbox.OutboxRegistrador;
import com.vitortheof.payment_service.repository.PagamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final OutboxRegistrador outboxRegistrador;
    private final ObjectMapper objectMapper;

    @Transactional
    public Pagamento aprovar(UUID pedidoId, BigDecimal valor) {
        Pagamento pagamento = Pagamento.aprovado(pedidoId, valor);
        pagamentoRepository.save(pagamento);

        UUID eventId = UUID.randomUUID();
        //reunindo os dados que serão enviados para os consumidores do evento
        var evento = new PagamentoAprovadoEvent(
                eventId, pagamento.getId(), pedidoId, valor, pagamento.getCriadoEm());

        //converter o evento em json para armazena-lo na outbox;
        String payload = objectMapper.writeValueAsString(evento);
        //registra oevento como pendente na msm transação do pagamento.
        outboxRegistrador.registrar(OutboxEvent.pendente(eventId, pagamento.getId(), "PagamentoAprovado", payload));


        return pagamento;
    }

}
