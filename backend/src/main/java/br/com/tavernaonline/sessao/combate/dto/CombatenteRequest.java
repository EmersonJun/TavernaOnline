package br.com.tavernaonline.sessao.combate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CombatenteRequest(
        @NotBlank @Size(max = 120) String nome,
        int iniciativa,
        UUID personagemId
) {
}
