package br.com.tavernaonline.sessao.combate;

import br.com.tavernaonline.common.EntidadeBase;
import br.com.tavernaonline.personagem.Personagem;
import br.com.tavernaonline.sessao.Sessao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participante_combate")
@Getter
@Setter
@NoArgsConstructor
public class ParticipanteCombate extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personagem_id")
    private Personagem personagem;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private int iniciativa;

    @Column(nullable = false)
    private int ordem;

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
