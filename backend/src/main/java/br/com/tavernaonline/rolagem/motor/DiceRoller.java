package br.com.tavernaonline.rolagem.motor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

@Component
public class DiceRoller {

    private final RandomGenerator random;
    private final DiceParser parser;

    public DiceRoller(RandomGenerator random, DiceParser parser) {
        this.random = random;
        this.parser = parser;
    }

    public ResultadoRolagem rolar(String notacao) {
        ExpressaoDado expressao = parser.parse(notacao);

        List<Integer> resultados = new ArrayList<>();
        int modificador = 0;
        int total = 0;

        for (Termo termo : expressao.termos()) {
            if (termo instanceof GrupoDados grupo) {
                for (int i = 0; i < grupo.quantidade(); i++) {
                    int valor = random.nextInt(grupo.faces()) + 1;
                    resultados.add(valor);
                    total += grupo.sinal() * valor;
                }
            } else if (termo instanceof Modificador mod) {
                int parcela = mod.sinal() * mod.valor();
                modificador += parcela;
                total += parcela;
            }
        }

        return new ResultadoRolagem(expressao.notacao(), resultados, modificador, total);
    }
}
