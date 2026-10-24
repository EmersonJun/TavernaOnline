package br.com.tavernaonline.rolagem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RolarRequest(
        @NotBlank @Size(max = 100) String notacao,
        @Size(max = 160) String motivo,
        boolean secreta,
        UUID personagemId
) {
}
