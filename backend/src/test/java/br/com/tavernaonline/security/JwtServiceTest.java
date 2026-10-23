package br.com.tavernaonline.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SEGREDO = "chave-de-teste-com-no-minimo-32-bytes-para-hmac-sha256";

    private final JwtService jwtService = new JwtService(SEGREDO, 120);

    @Test
    void deveGerarTokenComSubjectRecuperavel() {
        String token = jwtService.gerarToken("mestre@taverna.com");

        assertThat(jwtService.tokenValido(token)).isTrue();
        assertThat(jwtService.extrairSubject(token)).isEqualTo("mestre@taverna.com");
    }

    @Test
    void deveRejeitarTokenAdulterado() {
        String token = jwtService.gerarToken("mestre@taverna.com");
        String adulterado = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.tokenValido(adulterado)).isFalse();
    }

    @Test
    void deveRejeitarTokenAssinadoComOutroSegredo() {
        JwtService outro = new JwtService("outro-segredo-com-no-minimo-32-bytes-para-hmac-ok", 120);
        String token = outro.gerarToken("mestre@taverna.com");

        assertThat(jwtService.tokenValido(token)).isFalse();
    }

    @Test
    void deveConsiderarTokenExpiradoInvalido() {
        JwtService expirado = new JwtService(SEGREDO, -1);
        String token = expirado.gerarToken("mestre@taverna.com");

        assertThat(jwtService.tokenValido(token)).isFalse();
    }
}
