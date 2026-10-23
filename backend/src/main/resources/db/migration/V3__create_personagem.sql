CREATE TABLE personagem (
    id UUID PRIMARY KEY,
    campanha_id UUID NOT NULL,
    dono_id UUID NOT NULL,
    nome VARCHAR(120) NOT NULL,
    classe VARCHAR(80),
    raca VARCHAR(80),
    nivel INTEGER NOT NULL DEFAULT 1,
    pv_atual INTEGER NOT NULL DEFAULT 0,
    pv_maximo INTEGER NOT NULL DEFAULT 0,
    classe_armadura INTEGER NOT NULL DEFAULT 10,
    forca INTEGER NOT NULL DEFAULT 10,
    destreza INTEGER NOT NULL DEFAULT 10,
    constituicao INTEGER NOT NULL DEFAULT 10,
    inteligencia INTEGER NOT NULL DEFAULT 10,
    sabedoria INTEGER NOT NULL DEFAULT 10,
    carisma INTEGER NOT NULL DEFAULT 10,
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_personagem_campanha FOREIGN KEY (campanha_id) REFERENCES campanha (id) ON DELETE CASCADE,
    CONSTRAINT fk_personagem_dono FOREIGN KEY (dono_id) REFERENCES usuario (id)
);

CREATE INDEX idx_personagem_campanha ON personagem (campanha_id);
CREATE INDEX idx_personagem_dono ON personagem (dono_id);

CREATE TABLE personagem_pericia (
    personagem_id UUID NOT NULL,
    nome VARCHAR(80) NOT NULL,
    bonus INTEGER NOT NULL DEFAULT 0,
    proficiente BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_pericia_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE CASCADE
);

CREATE INDEX idx_pericia_personagem ON personagem_pericia (personagem_id);

CREATE TABLE personagem_inventario (
    personagem_id UUID NOT NULL,
    nome VARCHAR(120) NOT NULL,
    quantidade INTEGER NOT NULL DEFAULT 1,
    peso NUMERIC(8, 2) NOT NULL DEFAULT 0,
    descricao VARCHAR(500),
    CONSTRAINT fk_inventario_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id) ON DELETE CASCADE
);

CREATE INDEX idx_inventario_personagem ON personagem_inventario (personagem_id);
