package br.com.tavernaonline.rolagem.motor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiceParserTest {

    private final DiceParser parser = new DiceParser();

    @Test
    void deveInterpretarDadoSimplesSemQuantidade() {
        ExpressaoDado expressao = parser.parse("d20");

        assertThat(expressao.termos()).hasSize(1);
        assertThat(expressao.termos().get(0)).isEqualTo(new GrupoDados(1, 1, 20));
    }

    @Test
    void deveInterpretarQuantidadeFacesEModificador() {
        ExpressaoDado expressao = parser.parse("2d6+3");

        assertThat(expressao.termos())
                .containsExactly(new GrupoDados(1, 2, 6), new Modificador(1, 3));
    }

    @Test
    void deveInterpretarModificadorNegativo() {
        ExpressaoDado expressao = parser.parse("1d8-1");

        assertThat(expressao.termos())
                .containsExactly(new GrupoDados(1, 1, 8), new Modificador(-1, 1));
    }

    @Test
    void deveInterpretarExpressaoComposta() {
        ExpressaoDado expressao = parser.parse("1d20+1d4+2");

        assertThat(expressao.termos())
                .containsExactly(new GrupoDados(1, 1, 20), new GrupoDados(1, 1, 4), new Modificador(1, 2));
    }

    @Test
    void deveIgnorarEspacosEMaiusculas() {
        ExpressaoDado expressao = parser.parse("  2D6 + 3 ");

        assertThat(expressao.termos())
                .containsExactly(new GrupoDados(1, 2, 6), new Modificador(1, 3));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0d6", "2d", "abc", "d1", "1000d6", "101d6", "d", "2d6+", "+", "1d6++2"})
    void deveRejeitarNotacaoInvalida(String notacao) {
        assertThatThrownBy(() -> parser.parse(notacao))
                .isInstanceOf(NotacaoInvalidaException.class);
    }

    @Test
    void deveRejeitarNotacaoNulaOuVazia() {
        assertThatThrownBy(() -> parser.parse(null)).isInstanceOf(NotacaoInvalidaException.class);
        assertThatThrownBy(() -> parser.parse("   ")).isInstanceOf(NotacaoInvalidaException.class);
    }
}
