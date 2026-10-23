package br.com.tavernaonline.personagem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pericia {

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private int bonus;

    @Column(nullable = false)
    private boolean proficiente;
}
