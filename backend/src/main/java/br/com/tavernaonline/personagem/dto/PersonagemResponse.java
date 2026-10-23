package br.com.tavernaonline.personagem.dto;

import java.util.List;
import java.util.UUID;

public record PersonagemResponse(
        UUID id,
        UUID campanhaId,
        UUID donoId,
        String nome,
        String classe,
        String raca,
        int nivel,
        int pvAtual,
        int pvMaximo,
        int classeArmadura,
        AtributosDto atributos,
        List<PericiaDto> pericias,
        List<ItemInventarioDto> inventario,
        long versao
) {
}
