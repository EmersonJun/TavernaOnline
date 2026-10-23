package br.com.tavernaonline.personagem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ItemInventarioDto(
        @NotBlank @Size(max = 120) String nome,
        @Min(1) int quantidade,
        @NotNull @PositiveOrZero BigDecimal peso,
        @Size(max = 500) String descricao
) {
}
