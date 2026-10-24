package br.com.tavernaonline.sessao.dto;

import br.com.tavernaonline.sessao.StatusSessao;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoResponse(
        UUID id,
        UUID campanhaId,
        int numero,
        String titulo,
        StatusSessao status,
        LocalDateTime iniciadaEm,
        LocalDateTime encerradaEm,
        boolean combateAtivo,
        int rodadaAtual
) {
}
