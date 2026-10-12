package com.vitortheof.payment_service.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class OutboxCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(OutboxCleanupJob.class);

    private final OutboxEventRepository repository;
    private final Duration retencao;
    private final int tamanhoLote;

    public OutboxCleanupJob(OutboxEventRepository repository,
                            @Value("${outbox.limpeza.retencao}") Duration retencao,
                            @Value("${outbox.limpeza.tamanho-lote}") int tamanhoLote) {
        this.repository = repository;
        this.retencao = retencao;
        this.tamanhoLote = tamanhoLote;
    }

    @Scheduled(cron = "${outbox.limpeza.cron}")
    public void limpar() {
        LocalDateTime limite = LocalDateTime.now().minus(retencao);
        int total = 0;
        int apagados;

        do {
            apagados = repository.apagarPublicadosAntesDe(limite, tamanhoLote);
            total += apagados;
        } while (apagados == tamanhoLote);

        log.info("Limpeza da outbox: {} eventos publicados antes de {} removidos", total, limite);
    }

}
