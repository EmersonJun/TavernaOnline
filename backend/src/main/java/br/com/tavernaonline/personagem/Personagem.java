package br.com.tavernaonline.personagem;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.common.EntidadeBase;
import br.com.tavernaonline.usuario.Usuario;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personagem")
@Getter
@Setter
@NoArgsConstructor
public class Personagem extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campanha_id", nullable = false)
    private Campanha campanha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dono_id", nullable = false)
    private Usuario dono;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 80)
    private String classe;

    @Column(length = 80)
    private String raca;

    @Column(nullable = false)
    private int nivel = 1;

    @Column(name = "pv_atual", nullable = false)
    private int pvAtual;

    @Column(name = "pv_maximo", nullable = false)
    private int pvMaximo;

    @Column(name = "classe_armadura", nullable = false)
    private int classeArmadura = 10;

    @Embedded
    private Atributos atributos = new Atributos();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "personagem_pericia", joinColumns = @JoinColumn(name = "personagem_id"))
    private List<Pericia> pericias = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "personagem_inventario", joinColumns = @JoinColumn(name = "personagem_id"))
    private List<ItemInventario> inventario = new ArrayList<>();

    @Version
    @Column(name = "versao", nullable = false)
    private long versao;

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
