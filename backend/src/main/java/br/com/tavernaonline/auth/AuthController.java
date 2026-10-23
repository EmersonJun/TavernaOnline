package br.com.tavernaonline.auth;

import br.com.tavernaonline.auth.dto.CadastroRequest;
import br.com.tavernaonline.auth.dto.LoginRequest;
import br.com.tavernaonline.auth.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacao")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cria um usuario e retorna um token JWT")
    public ResponseEntity<TokenResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        TokenResponse resposta = authService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuario e retorna um token JWT")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.autenticar(request));
    }
}
