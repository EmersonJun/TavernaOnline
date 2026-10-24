package br.com.tavernaonline.sessao;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.common.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
@Getter
@Setter
@NoArgsConstructor
public class Sessao extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campanha_id", nullable = false)
    private Campanha campanha;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSessao status = StatusSessao.AGENDADA;

    @Column(name = "iniciada_em")
    private LocalDateTime iniciadaEm;

    @Column(name = "encerrada_em")
    private LocalDateTime encerradaEm;

    @Column(name = "combate_ativo", nullable = false)
    private boolean combateAtivo;

    @Column(name = "rodada_atual", nullable = false)
    private int rodadaAtual;

    @Column(name = "indice_turno_atual", nullable = false)
    private int indiceTurnoAtual;

    public Sessao(Campanha campanha, int numero, String titulo) {
        this.campanha = campanha;
        this.numero = numero;
        this.titulo = titulo;
        this.status = StatusSessao.AGENDADA;
    }

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
