package br.com.tavernaonline.campanha.dto;

import br.com.tavernaonline.campanha.Papel;

import java.util.UUID;

public record ParticipanteResponse(
        UUID usuarioId,
        String nome,
        String email,
        Papel papel
) {
}
