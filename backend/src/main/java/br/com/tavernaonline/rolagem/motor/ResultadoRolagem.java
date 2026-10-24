package br.com.tavernaonline.rolagem.motor;

import java.util.List;

public record ResultadoRolagem(
        String notacao,
        List<Integer> resultados,
        int modificador,
        int total
) {
}
