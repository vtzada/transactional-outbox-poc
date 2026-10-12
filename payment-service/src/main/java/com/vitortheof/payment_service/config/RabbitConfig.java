package com.vitortheof.payment_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pagamentos.exchange";
    public static final String ROUTING_KEY = "pagamento.aprovado";

    @Bean
    public TopicExchange pagamentosExchange() {
        return new TopicExchange(EXCHANGE);
    }
}
