package br.com.tavernaonline.rolagem.motor;

import org.junit.jupiter.api.Test;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class DiceRollerTest {

    private static final class DadoFixo implements RandomGenerator {
        private final int[] valores;
        private int indice;

        private DadoFixo(int... valores) {
            this.valores = valores;
        }

        @Override
        public int nextInt(int bound) {
            return valores[indice++] - 1;
        }

        @Override
        public long nextLong() {
            return 0L;
        }
    }

    private DiceRoller roller(int... valores) {
        return new DiceRoller(new DadoFixo(valores), new DiceParser());
    }

    @Test
    void deveSomarDadosEModificador() {
        ResultadoRolagem resultado = roller(4, 2).rolar("2d6+3");

        assertThat(resultado.resultados()).containsExactly(4, 2);
        assertThat(resultado.modificador()).isEqualTo(3);
        assertThat(resultado.total()).isEqualTo(9);
    }

    @Test
    void deveAplicarModificadorNegativo() {
        ResultadoRolagem resultado = roller(15).rolar("1d20-1");

        assertThat(resultado.resultados()).containsExactly(15);
        assertThat(resultado.total()).isEqualTo(14);
    }

    @Test
    void deveResolverExpressaoComposta() {
        ResultadoRolagem resultado = roller(20, 3).rolar("1d20+1d4+2");

        assertThat(resultado.resultados()).containsExactly(20, 3);
        assertThat(resultado.modificador()).isEqualTo(2);
        assertThat(resultado.total()).isEqualTo(25);
    }

    @Test
    void deveSubtrairUmGrupoDeDados() {
        ResultadoRolagem resultado = roller(8, 3).rolar("1d8-1d4");

        assertThat(resultado.resultados()).containsExactly(8, 3);
        assertThat(resultado.modificador()).isZero();
        assertThat(resultado.total()).isEqualTo(5);
    }

    @Test
    void deveRolarVariosDadosDoMesmoGrupo() {
        ResultadoRolagem resultado = roller(6, 1, 5, 2).rolar("4d6");

        assertThat(resultado.resultados()).containsExactly(6, 1, 5, 2);
        assertThat(resultado.total()).isEqualTo(14);
    }
}
