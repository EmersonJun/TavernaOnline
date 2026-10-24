package br.com.tavernaonline.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnviarMensagemRequest(
        @NotBlank @Size(max = 2000) String conteudo
) {
}
