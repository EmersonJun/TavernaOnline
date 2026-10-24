package br.com.tavernaonline.sessao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarSessaoRequest(
        @NotBlank @Size(max = 160) String titulo
) {
}
