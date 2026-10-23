package br.com.tavernaonline.personagem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PericiaDto(
        @NotBlank @Size(max = 80) String nome,
        int bonus,
        boolean proficiente
) {
}
