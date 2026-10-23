package br.com.tavernaonline.personagem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record AtributosDto(
        @Min(1) @Max(30) int forca,
        @Min(1) @Max(30) int destreza,
        @Min(1) @Max(30) int constituicao,
        @Min(1) @Max(30) int inteligencia,
        @Min(1) @Max(30) int sabedoria,
        @Min(1) @Max(30) int carisma
) {
}
