package br.com.tavernaonline.diario.dto;

import br.com.tavernaonline.diario.VisibilidadeAnotacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnotacaoResponse(
        UUID id,
        UUID campanhaId,
        UUID sessaoId,
        UUID autorId,
        String autorNome,
        String titulo,
        String conteudo,
        VisibilidadeAnotacao visibilidade,
        LocalDateTime criadoEm
) {
}
