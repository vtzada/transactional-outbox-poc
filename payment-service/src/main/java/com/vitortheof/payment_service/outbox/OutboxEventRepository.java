package com.vitortheof.payment_service.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
