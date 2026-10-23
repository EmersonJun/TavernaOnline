package br.com.tavernaonline.personagem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemInventario {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal peso;

    @Column(length = 500)
    private String descricao;
}
