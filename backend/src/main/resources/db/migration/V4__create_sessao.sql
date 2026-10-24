CREATE TABLE sessao (
    id UUID PRIMARY KEY,
    campanha_id UUID NOT NULL,
    numero INTEGER NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    status VARCHAR(20) NOT NULL,
    iniciada_em TIMESTAMP,
    encerrada_em TIMESTAMP,
    combate_ativo BOOLEAN NOT NULL DEFAULT FALSE,
    rodada_atual INTEGER NOT NULL DEFAULT 0,
    indice_turno_atual INTEGER NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_sessao_campanha FOREIGN KEY (campanha_id) REFERENCES campanha (id) ON DELETE CASCADE,
    CONSTRAINT uk_sessao_campanha_numero UNIQUE (campanha_id, numero)
);

CREATE INDEX idx_sessao_campanha ON sessao (campanha_id);
