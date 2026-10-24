package br.com.tavernaonline.sessao.combate;

import br.com.tavernaonline.sessao.combate.dto.CombateResponse;
import br.com.tavernaonline.sessao.combate.dto.IniciarCombateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessoes/{sessaoId}/combate")
@Tag(name = "Combate")
public class CombateController {

    private final CombateService combateService;

    public CombateController(CombateService combateService) {
        this.combateService = combateService;
    }

    @GetMapping
    @Operation(summary = "Estado atual do combate")
    public ResponseEntity<CombateResponse> estado(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(combateService.estado(sessaoId));
    }

    @PostMapping("/iniciar")
    @Operation(summary = "Inicia o combate com a lista de iniciativa (apenas mestre)")
    public ResponseEntity<CombateResponse> iniciar(
            @PathVariable UUID sessaoId,
            @Valid @RequestBody IniciarCombateRequest request) {
        return ResponseEntity.ok(combateService.iniciar(sessaoId, request));
    }

    @PostMapping("/proximo-turno")
    @Operation(summary = "Avanca para o proximo turno (apenas mestre)")
    public ResponseEntity<CombateResponse> proximoTurno(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(combateService.proximoTurno(sessaoId));
    }

    @PostMapping("/encerrar")
    @Operation(summary = "Encerra o combate (apenas mestre)")
    public ResponseEntity<CombateResponse> encerrar(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(combateService.encerrar(sessaoId));
    }
}
