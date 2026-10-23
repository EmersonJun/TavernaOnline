package br.com.tavernaonline.campanha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarCampanhaRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 2000) String descricao,
        @NotBlank @Size(max = 80) String sistema
) {
}
