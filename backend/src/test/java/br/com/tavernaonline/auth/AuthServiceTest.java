package br.com.tavernaonline.auth;

import br.com.tavernaonline.auth.dto.CadastroRequest;
import br.com.tavernaonline.auth.dto.LoginRequest;
import br.com.tavernaonline.auth.dto.TokenResponse;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.security.JwtService;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void cadastrarDeveSalvarUsuarioComSenhaCriptografadaERetornarToken() {
        CadastroRequest request = new CadastroRequest("Mestre", "mestre@taverna.com", "senha1234");
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode("senha1234")).thenReturn("hash");
        when(jwtService.gerarToken("mestre@taverna.com")).thenReturn("token-jwt");
        when(jwtService.getExpiracaoMinutos()).thenReturn(120L);

        TokenResponse resposta = authService.cadastrar(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getSenhaHash()).isEqualTo("hash");
        assertThat(captor.getValue().getEmail()).isEqualTo("mestre@taverna.com");
        assertThat(resposta.token()).isEqualTo("token-jwt");
        assertThat(resposta.tipo()).isEqualTo("Bearer");
        assertThat(resposta.expiraEmMinutos()).isEqualTo(120L);
    }

    @Test
    void cadastrarDeveFalharQuandoEmailJaExiste() {
        CadastroRequest request = new CadastroRequest("Mestre", "mestre@taverna.com", "senha1234");
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.cadastrar(request))
                .isInstanceOf(RegraNegocioException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void autenticarDeveRetornarTokenQuandoCredenciaisValidas() {
        LoginRequest request = new LoginRequest("mestre@taverna.com", "senha1234");
        when(jwtService.gerarToken("mestre@taverna.com")).thenReturn("token-jwt");
        when(jwtService.getExpiracaoMinutos()).thenReturn(120L);

        TokenResponse resposta = authService.autenticar(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(resposta.token()).isEqualTo("token-jwt");
    }

    @Test
    void autenticarDevePropagarErroQuandoCredenciaisInvalidas() {
        LoginRequest request = new LoginRequest("mestre@taverna.com", "errada");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("invalida"));

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).gerarToken(any());
    }
}
