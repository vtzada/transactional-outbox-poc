CREATE TABLE pedidos
(
    id            UUID PRIMARY KEY,
    valor         NUMERIC(12, 2) NOT NULL,
    status        VARCHAR(30)    NOT NULL,
    criado_em     TIMESTAMP      NOT NULL,
    atualizado_em TIMESTAMP      NOT NULL
);

CREATE TABLE eventos_processados
(
    event_id      UUID PRIMARY KEY,
    processado_em TIMESTAMP NOT NULL
);