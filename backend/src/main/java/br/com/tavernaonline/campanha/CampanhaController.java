package br.com.tavernaonline.campanha;

import br.com.tavernaonline.campanha.dto.CampanhaResponse;
import br.com.tavernaonline.campanha.dto.CriarCampanhaRequest;
import br.com.tavernaonline.campanha.dto.EntrarCampanhaRequest;
import br.com.tavernaonline.campanha.dto.ParticipanteResponse;
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
@RequestMapping("/api/campanhas")
@Tag(name = "Campanhas")
public class CampanhaController {

    private final CampanhaService campanhaService;

    public CampanhaController(CampanhaService campanhaService) {
        this.campanhaService = campanhaService;
    }

    @PostMapping
    @Operation(summary = "Cria uma campanha e torna o usuario atual o mestre")
    public ResponseEntity<CampanhaResponse> criar(@Valid @RequestBody CriarCampanhaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campanhaService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Lista as campanhas das quais o usuario atual participa")
    public ResponseEntity<List<CampanhaResponse>> listarMinhas() {
        return ResponseEntity.ok(campanhaService.listarMinhas());
    }

    @PostMapping("/entrar")
    @Operation(summary = "Entra em uma campanha usando o codigo de convite")
    public ResponseEntity<CampanhaResponse> entrar(@Valid @RequestBody EntrarCampanhaRequest request) {
        return ResponseEntity.ok(campanhaService.entrarPorCodigo(request));
    }

    @GetMapping("/{campanhaId}/participantes")
    @Operation(summary = "Lista os participantes de uma campanha")
    public ResponseEntity<List<ParticipanteResponse>> participantes(@PathVariable UUID campanhaId) {
        return ResponseEntity.ok(campanhaService.listarParticipantes(campanhaId));
    }
}
