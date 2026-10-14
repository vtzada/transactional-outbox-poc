package com.vitortheof.payment_service.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxRegistrador {

    private final OutboxEventRepository repository;
    private final boolean modoDebezium;

    public OutboxRegistrador(OutboxEventRepository repository,
                             @Value("${outbox.publicacao.modo:polling}") String modo) {
        this.repository = repository;
        this.modoDebezium = "debezium".equals(modo);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void registrar(OutboxEvent evento) {
        if (!modoDebezium) {
            repository.save(evento); // o worker lê depois
            return;
        }

        OutboxEvent salvo = repository.saveAndFlush(evento); // INSERT vai para o WAL
        repository.delete(salvo);                            // DELETE no commit; tabela fica vazia
    }

}
