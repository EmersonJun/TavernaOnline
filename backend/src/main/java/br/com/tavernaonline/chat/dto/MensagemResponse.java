package br.com.tavernaonline.chat.dto;

import br.com.tavernaonline.chat.TipoMensagem;

import java.time.LocalDateTime;
import java.util.UUID;

public record MensagemResponse(
        UUID id,
        UUID sessaoId,
        UUID autorId,
        String autorNome,
        TipoMensagem tipo,
        String conteudo,
        LocalDateTime criadoEm
) {
}
