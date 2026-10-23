package br.com.tavernaonline.personagem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PersonagemRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 80) String classe,
        @Size(max = 80) String raca,
        @Min(1) int nivel,
        @PositiveOrZero int pvAtual,
        @PositiveOrZero int pvMaximo,
        @PositiveOrZero int classeArmadura,
        @NotNull @Valid AtributosDto atributos,
        @Valid List<PericiaDto> pericias,
        @Valid List<ItemInventarioDto> inventario
) {
}
