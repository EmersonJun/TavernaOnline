package br.com.tavernaonline.diario;

import br.com.tavernaonline.diario.dto.AnotacaoRequest;
import br.com.tavernaonline.diario.dto.AnotacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Diario da campanha")
public class AnotacaoController {

    private final AnotacaoService anotacaoService;

    public AnotacaoController(AnotacaoService anotacaoService) {
        this.anotacaoService = anotacaoService;
    }

    @PostMapping("/campanhas/{campanhaId}/anotacoes")
    @Operation(summary = "Cria uma anotacao no diario da campanha")
    public ResponseEntity<AnotacaoResponse> criar(
            @PathVariable UUID campanhaId,
            @Valid @RequestBody AnotacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anotacaoService.criar(campanhaId, request));
    }

    @GetMapping("/campanhas/{campanhaId}/anotacoes")
    @Operation(summary = "Lista as anotacoes visiveis para o usuario atual")
    public ResponseEntity<List<AnotacaoResponse>> listar(@PathVariable UUID campanhaId) {
        return ResponseEntity.ok(anotacaoService.listarPorCampanha(campanhaId));
    }

    @PutMapping("/anotacoes/{anotacaoId}")
    @Operation(summary = "Atualiza uma anotacao")
    public ResponseEntity<AnotacaoResponse> atualizar(
            @PathVariable UUID anotacaoId,
            @Valid @RequestBody AnotacaoRequest request) {
        return ResponseEntity.ok(anotacaoService.atualizar(anotacaoId, request));
    }

    @DeleteMapping("/anotacoes/{anotacaoId}")
    @Operation(summary = "Remove uma anotacao")
    public ResponseEntity<Void> excluir(@PathVariable UUID anotacaoId) {
        anotacaoService.excluir(anotacaoId);
        return ResponseEntity.noContent().build();
    }
}
