package br.com.tavernaonline.common;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResposta(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalido> camposInvalidos
) {
    public record CampoInvalido(String campo, String mensagem) {
    }

    public static ErroResposta de(int status, String erro, String mensagem, String caminho) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, caminho, List.of());
    }
}
