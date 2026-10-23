package br.com.tavernaonline.personagem;

import br.com.tavernaonline.personagem.dto.PersonagemRequest;
import br.com.tavernaonline.personagem.dto.PersonagemResponse;
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
@Tag(name = "Personagens")
public class PersonagemController {

    private final PersonagemService personagemService;

    public PersonagemController(PersonagemService personagemService) {
        this.personagemService = personagemService;
    }

    @PostMapping("/campanhas/{campanhaId}/personagens")
    @Operation(summary = "Cria uma ficha de personagem na campanha")
    public ResponseEntity<PersonagemResponse> criar(
            @PathVariable UUID campanhaId,
            @Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personagemService.criar(campanhaId, request));
    }

    @GetMapping("/campanhas/{campanhaId}/personagens")
    @Operation(summary = "Lista as fichas de personagem da campanha")
    public ResponseEntity<List<PersonagemResponse>> listar(@PathVariable UUID campanhaId) {
        return ResponseEntity.ok(personagemService.listarPorCampanha(campanhaId));
    }

    @GetMapping("/personagens/{personagemId}")
    @Operation(summary = "Busca uma ficha de personagem")
    public ResponseEntity<PersonagemResponse> buscar(@PathVariable UUID personagemId) {
        return ResponseEntity.ok(personagemService.buscar(personagemId));
    }

    @PutMapping("/personagens/{personagemId}")
    @Operation(summary = "Atualiza uma ficha de personagem")
    public ResponseEntity<PersonagemResponse> atualizar(
            @PathVariable UUID personagemId,
            @Valid @RequestBody PersonagemRequest request) {
        return ResponseEntity.ok(personagemService.atualizar(personagemId, request));
    }

    @DeleteMapping("/personagens/{personagemId}")
    @Operation(summary = "Remove uma ficha de personagem")
    public ResponseEntity<Void> excluir(@PathVariable UUID personagemId) {
        personagemService.excluir(personagemId);
        return ResponseEntity.noContent().build();
    }
}
