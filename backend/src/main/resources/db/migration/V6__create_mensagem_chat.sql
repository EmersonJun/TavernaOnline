CREATE TABLE mensagem_chat (
    id UUID PRIMARY KEY,
    sessao_id UUID NOT NULL,
    autor_id UUID,
    tipo VARCHAR(20) NOT NULL,
    conteudo VARCHAR(2000) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_mensagem_sessao FOREIGN KEY (sessao_id) REFERENCES sessao (id) ON DELETE CASCADE,
    CONSTRAINT fk_mensagem_autor FOREIGN KEY (autor_id) REFERENCES usuario (id)
);

CREATE INDEX idx_mensagem_sessao ON mensagem_chat (sessao_id);
