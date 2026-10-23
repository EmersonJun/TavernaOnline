package br.com.tavernaonline.personagem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Atributos {

    @Column(nullable = false)
    private int forca = 10;

    @Column(nullable = false)
    private int destreza = 10;

    @Column(nullable = false)
    private int constituicao = 10;

    @Column(nullable = false)
    private int inteligencia = 10;

    @Column(nullable = false)
    private int sabedoria = 10;

    @Column(nullable = false)
    private int carisma = 10;
}
