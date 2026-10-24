CREATE TABLE rolagem (
    id UUID PRIMARY KEY,
    sessao_id UUID NOT NULL,
    autor_id UUID NOT NULL,
    personagem_id UUID,
    notacao VARCHAR(100) NOT NULL,
    resultados VARCHAR(500) NOT NULL,
    modificador INTEGER NOT NULL,
    total INTEGER NOT NULL,
    motivo VARCHAR(160),
    secreta BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_rolagem_sessao FOREIGN KEY (sessao_id) REFERENCES sessao (id) ON DELETE CASCADE,
    CONSTRAINT fk_rolagem_autor FOREIGN KEY (autor_id) REFERENCES usuario (id),
    CONSTRAINT fk_rolagem_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE SET NULL
);

CREATE INDEX idx_rolagem_sessao ON rolagem (sessao_id);
