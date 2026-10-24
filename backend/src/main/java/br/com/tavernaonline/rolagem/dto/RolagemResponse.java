package br.com.tavernaonline.rolagem.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RolagemResponse(
        UUID id,
        UUID sessaoId,
        UUID autorId,
        String autorNome,
        UUID personagemId,
        String notacao,
        List<Integer> resultados,
        int modificador,
        int total,
        String motivo,
        boolean secreta,
        LocalDateTime criadoEm
) {
}
