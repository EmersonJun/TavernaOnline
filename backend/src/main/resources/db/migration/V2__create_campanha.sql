CREATE TABLE campanha (
    id UUID PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(2000),
    sistema VARCHAR(80) NOT NULL,
    codigo_convite VARCHAR(8) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT uk_campanha_codigo_convite UNIQUE (codigo_convite)
);

CREATE TABLE participacao (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    campanha_id UUID NOT NULL,
    papel VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_participacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_participacao_campanha FOREIGN KEY (campanha_id) REFERENCES campanha (id) ON DELETE CASCADE,
    CONSTRAINT uk_participacao_usuario_campanha UNIQUE (usuario_id, campanha_id)
);

CREATE INDEX idx_participacao_campanha ON participacao (campanha_id);
CREATE INDEX idx_participacao_usuario ON participacao (usuario_id);
