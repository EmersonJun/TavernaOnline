package br.com.tavernaonline.chat;

import br.com.tavernaonline.chat.dto.MensagemResponse;
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
@Tag(name = "Chat")
public class ChatController {

    private final ChatService chatService;
    private final UsuarioAtual usuarioAtual;

    public ChatController(ChatService chatService, UsuarioAtual usuarioAtual) {
        this.chatService = chatService;
        this.usuarioAtual = usuarioAtual;
    }

    @GetMapping("/sessoes/{sessaoId}/mensagens")
    @Operation(summary = "Historico de mensagens da sessao")
    public ResponseEntity<List<MensagemResponse>> historico(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(chatService.historico(sessaoId, usuarioAtual.getEmail()));
    }
}
