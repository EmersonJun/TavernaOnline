package br.com.tavernaonline.rolagem;

import br.com.tavernaonline.rolagem.dto.RolagemResponse;
import br.com.tavernaonline.security.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Rolagens")
public class RolagemController {

    private final RolagemService rolagemService;
    private final UsuarioAtual usuarioAtual;

    public RolagemController(RolagemService rolagemService, UsuarioAtual usuarioAtual) {
        this.rolagemService = rolagemService;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping("/sessoes/{sessaoId}/rolagens")
    @Operation(summary = "Historico das rolagens publicas da sessao")
    public ResponseEntity<List<RolagemResponse>> historico(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(rolagemService.historicoPublico(sessaoId, usuarioAtual.getEmail()));
    }
}
