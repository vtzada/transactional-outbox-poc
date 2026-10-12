package com.vitortheof.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pagamentos.exchange";
    public static final String ROUTING_KEY = "pagamento.aprovado";
    public static final String QUEUE = "pedidos.pagamento-aprovado";

    @Bean
    public TopicExchange pagamentosExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue pagamentoAprovadoQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding(Queue pagamentoAprovadoQueue, TopicExchange pagamentosExchange) {
        return BindingBuilder.bind(pagamentoAprovadoQueue).to(pagamentosExchange).with(ROUTING_KEY);
    }
}

