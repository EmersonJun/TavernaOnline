package br.com.tavernaonline.sessao.combate.dto;

import java.util.UUID;

public record CombatenteResponse(
        UUID id,
        String nome,
        int iniciativa,
        int ordem,
        UUID personagemId
) {
}
