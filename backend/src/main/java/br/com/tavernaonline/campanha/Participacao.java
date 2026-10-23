package br.com.tavernaonline.campanha;

import br.com.tavernaonline.common.EntidadeBase;
import br.com.tavernaonline.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participacao", uniqueConstraints =
        @UniqueConstraint(name = "uk_participacao_usuario_campanha", columnNames = {"usuario_id", "campanha_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Participacao extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campanha_id", nullable = false)
    private Campanha campanha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Papel papel;

    public Participacao(Usuario usuario, Campanha campanha, Papel papel) {
        this.usuario = usuario;
        this.campanha = campanha;
        this.papel = papel;
    }

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
