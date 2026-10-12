CREATE INDEX idx_outbox_publicados
    ON outbox_events (publicado_em)
    WHERE status = 'PUBLICADO';