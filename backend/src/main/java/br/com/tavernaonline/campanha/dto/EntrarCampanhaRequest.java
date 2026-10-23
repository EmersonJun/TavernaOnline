package br.com.tavernaonline.campanha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntrarCampanhaRequest(
        @NotBlank @Size(min = 8, max = 8) String codigoConvite
) {
}
