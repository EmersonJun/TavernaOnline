package br.com.tavernaonline.rolagem;

import br.com.tavernaonline.common.EntidadeBase;
import br.com.tavernaonline.common.ListaInteirosConverter;
import br.com.tavernaonline.personagem.Personagem;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rolagem")
@Getter
@Setter
@NoArgsConstructor
public class Rolagem extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personagem_id")
    private Personagem personagem;

    @Column(nullable = false, length = 100)
    private String notacao;

    @Convert(converter = ListaInteirosConverter.class)
    @Column(nullable = false, length = 500)
    private List<Integer> resultados = new ArrayList<>();

    @Column(nullable = false)
    private int modificador;

    @Column(nullable = false)
    private int total;

    @Column(length = 160)
    private String motivo;

    @Column(nullable = false)
    private boolean secreta;

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
