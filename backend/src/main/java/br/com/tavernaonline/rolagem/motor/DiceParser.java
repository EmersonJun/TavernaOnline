package br.com.tavernaonline.rolagem.motor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DiceParser {

    public static final int MAX_QUANTIDADE = 100;
    public static final int MAX_FACES = 1000;

    private static final Pattern CARACTERES_VALIDOS = Pattern.compile("[0-9d+-]+");
    private static final Pattern TOKEN = Pattern.compile("([+-])(\\d*d\\d+|\\d+)");

    public ExpressaoDado parse(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            throw new NotacaoInvalidaException("Notacao vazia");
        }

        String limpo = entrada.replaceAll("\\s+", "").toLowerCase();
        if (!CARACTERES_VALIDOS.matcher(limpo).matches()) {
            throw new NotacaoInvalidaException("Notacao contem caracteres invalidos: " + entrada);
        }
        if (limpo.charAt(0) != '+' && limpo.charAt(0) != '-') {
            limpo = "+" + limpo;
        }

        List<Termo> termos = new ArrayList<>();
        Matcher matcher = TOKEN.matcher(limpo);
        int posicao = 0;
        while (matcher.find()) {
            if (matcher.start() != posicao) {
                throw new NotacaoInvalidaException("Notacao mal formada: " + entrada);
            }
            posicao = matcher.end();
            termos.add(interpretarTermo(matcher.group(1), matcher.group(2), entrada));
        }

        if (posicao != limpo.length() || termos.isEmpty()) {
            throw new NotacaoInvalidaException("Notacao mal formada: " + entrada);
        }
        return new ExpressaoDado(entrada, termos);
    }

    private Termo interpretarTermo(String sinalTexto, String corpo, String entrada) {
        int sinal = sinalTexto.equals("-") ? -1 : 1;
        if (corpo.contains("d")) {
            String[] partes = corpo.split("d", 2);
            int quantidade = partes[0].isEmpty() ? 1 : paraInteiro(partes[0], entrada);
            int faces = paraInteiro(partes[1], entrada);
            validarGrupo(quantidade, faces, entrada);
            return new GrupoDados(sinal, quantidade, faces);
        }
        return new Modificador(sinal, paraInteiro(corpo, entrada));
    }

    private void validarGrupo(int quantidade, int faces, String entrada) {
        if (quantidade < 1 || quantidade > MAX_QUANTIDADE) {
            throw new NotacaoInvalidaException(
                    "Quantidade de dados deve ficar entre 1 e " + MAX_QUANTIDADE + ": " + entrada);
        }
        if (faces < 2 || faces > MAX_FACES) {
            throw new NotacaoInvalidaException(
                    "Numero de faces deve ficar entre 2 e " + MAX_FACES + ": " + entrada);
        }
    }

    private int paraInteiro(String valor, String entrada) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            throw new NotacaoInvalidaException("Numero invalido na notacao: " + entrada);
        }
    }
}
