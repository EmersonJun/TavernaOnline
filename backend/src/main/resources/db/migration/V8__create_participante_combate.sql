CREATE TABLE participante_combate (
    id UUID PRIMARY KEY,
    sessao_id UUID NOT NULL,
    personagem_id UUID,
    nome VARCHAR(120) NOT NULL,
    iniciativa INTEGER NOT NULL,
    ordem INTEGER NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_combatente_sessao FOREIGN KEY (sessao_id) REFERENCES sessao (id) ON DELETE CASCADE,
    CONSTRAINT fk_combatente_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE SET NULL
);

CREATE INDEX idx_combatente_sessao ON participante_combate (sessao_id);
