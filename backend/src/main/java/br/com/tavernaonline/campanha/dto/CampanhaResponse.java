package br.com.tavernaonline.campanha.dto;

import br.com.tavernaonline.campanha.Papel;

import java.util.UUID;

public record CampanhaResponse(
        UUID id,
        String nome,
        String descricao,
        String sistema,
        String codigoConvite,
        boolean ativa,
        Papel meuPapel
) {
}
