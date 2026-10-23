package br.com.tavernaonline.campanha;

import br.com.tavernaonline.common.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campanha")
@Getter
@Setter
@NoArgsConstructor
public class Campanha extends EntidadeBase {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 2000)
    private String descricao;

    @Column(nullable = false, length = 80)
    private String sistema;

    @Column(name = "codigo_convite", nullable = false, length = 8, unique = true)
    private String codigoConvite;

    @Column(nullable = false)
    private boolean ativa = true;

    public Campanha(String nome, String descricao, String sistema, String codigoConvite) {
        this.nome = nome;
        this.descricao = descricao;
        this.sistema = sistema;
        this.codigoConvite = codigoConvite;
        this.ativa = true;
    }

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
