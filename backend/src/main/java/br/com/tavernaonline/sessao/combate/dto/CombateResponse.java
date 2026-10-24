package br.com.tavernaonline.sessao.combate.dto;

import java.util.List;
import java.util.UUID;

public record CombateResponse(
        UUID sessaoId,
        boolean combateAtivo,
        int rodadaAtual,
        int indiceTurnoAtual,
        UUID participanteDaVezId,
        List<CombatenteResponse> ordem
) {
}
