package br.com.tavernaonline.sessao.combate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record IniciarCombateRequest(
        @NotEmpty @Valid List<CombatenteRequest> combatentes
) {
}
