package com.vitortheof.orderservice.messaging;

import com.vitortheof.orderservice.config.RabbitConfig;
import com.vitortheof.orderservice.event.PagamentoAprovadoEvent;
import com.vitortheof.orderservice.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PagamentoAprovadoListener {

    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.QUEUE) // O Spring chama este método quando uma mensagem é entregue.
    public void consumir(Message message) {
        PagamentoAprovadoEvent evento =
                objectMapper.readValue(message.getBody(), PagamentoAprovadoEvent.class); // transforma o json num objeto java com os dados do pagamento
        pedidoService.confirmarPagamento(evento); //delega ao serviço a att do pedido para pago, e o registry do evento, para evitar processamento repetido
    }
}
