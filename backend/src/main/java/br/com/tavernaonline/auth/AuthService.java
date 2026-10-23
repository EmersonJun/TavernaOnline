package br.com.tavernaonline.auth;

import br.com.tavernaonline.auth.dto.CadastroRequest;
import br.com.tavernaonline.auth.dto.LoginRequest;
import br.com.tavernaonline.auth.dto.TokenResponse;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.security.JwtService;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Ja existe um usuario com este email");
        }
        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.nome(), request.email(), senhaHash);
        usuarioRepository.save(usuario);
        return gerarResposta(usuario.getEmail());
    }

    public TokenResponse autenticar(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        return gerarResposta(request.email());
    }

    private TokenResponse gerarResposta(String email) {
        String token = jwtService.gerarToken(email);
        return TokenResponse.bearer(token, jwtService.getExpiracaoMinutos());
    }
}
