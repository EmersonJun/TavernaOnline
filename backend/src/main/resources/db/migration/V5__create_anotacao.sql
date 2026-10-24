CREATE TABLE anotacao (
    id UUID PRIMARY KEY,
    campanha_id UUID NOT NULL,
    sessao_id UUID,
    autor_id UUID NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    conteudo TEXT NOT NULL,
    visibilidade VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_anotacao_campanha FOREIGN KEY (campanha_id) REFERENCES campanha (id) ON DELETE CASCADE,
    CONSTRAINT fk_anotacao_sessao FOREIGN KEY (sessao_id) REFERENCES sessao (id) ON DELETE SET NULL,
    CONSTRAINT fk_anotacao_autor FOREIGN KEY (autor_id) REFERENCES usuario (id)
);

CREATE INDEX idx_anotacao_campanha ON anotacao (campanha_id);
CREATE INDEX idx_anotacao_sessao ON anotacao (sessao_id);
