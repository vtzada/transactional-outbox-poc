CREATE TABLE pagamentos
(
    id        UUID PRIMARY KEY,
    pedido_id UUID           NOT NULL,
    valor     NUMERIC(12, 2) NOT NULL,
    status    VARCHAR(20)    NOT NULL,
    criado_em TIMESTAMP      NOT NULL
);

CREATE TABLE outbox_events
(
    id           UUID PRIMARY KEY,
    aggregate_id UUID         NOT NULL,
    tipo         VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    criado_em    TIMESTAMP    NOT NULL,
    publicado_em TIMESTAMP
);

CREATE INDEX idx_outbox_pendentes
    ON outbox_events (criado_em) WHERE status = 'PENDENTE';