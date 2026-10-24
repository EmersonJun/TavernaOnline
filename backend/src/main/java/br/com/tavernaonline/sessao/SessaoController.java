package br.com.tavernaonline.sessao;

import br.com.tavernaonline.sessao.dto.CriarSessaoRequest;
import br.com.tavernaonline.sessao.dto.SessaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping("/campanhas/{campanhaId}/sessoes")
    @Operation(summary = "Cria uma sessao na campanha (apenas mestre)")
    public ResponseEntity<SessaoResponse> criar(
            @PathVariable UUID campanhaId,
            @Valid @RequestBody CriarSessaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoService.criar(campanhaId, request));
    }

    @GetMapping("/campanhas/{campanhaId}/sessoes")
    @Operation(summary = "Lista as sessoes da campanha")
    public ResponseEntity<List<SessaoResponse>> listar(@PathVariable UUID campanhaId) {
        return ResponseEntity.ok(sessaoService.listarPorCampanha(campanhaId));
    }

    @PostMapping("/sessoes/{sessaoId}/iniciar")
    @Operation(summary = "Inicia uma sessao (apenas mestre)")
    public ResponseEntity<SessaoResponse> iniciar(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(sessaoService.iniciar(sessaoId));
    }

    @PostMapping("/sessoes/{sessaoId}/encerrar")
    @Operation(summary = "Encerra uma sessao (apenas mestre)")
    public ResponseEntity<SessaoResponse> encerrar(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(sessaoService.encerrar(sessaoId));
    }
}
