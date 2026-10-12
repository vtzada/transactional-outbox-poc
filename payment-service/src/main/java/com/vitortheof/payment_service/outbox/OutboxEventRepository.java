package com.vitortheof.payment_service.outbox;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
            SELECT * FROM outbox_events
            WHERE status = 'PENDENTE'
            ORDER BY criado_em
            LIMIT :limite
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> buscarPendentesParaPublicar(@Param("limite") int limite);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM outbox_events
        WHERE id IN (
            SELECT id FROM outbox_events
            WHERE status = 'PUBLICADO'
              AND publicado_em < :limite
            ORDER BY publicado_em
            LIMIT :tamanhoLote
        )
        """, nativeQuery = true)
    int apagarPublicadosAntesDe(@Param("limite") LocalDateTime limite,
                                @Param("tamanhoLote") int tamanhoLote);
}
