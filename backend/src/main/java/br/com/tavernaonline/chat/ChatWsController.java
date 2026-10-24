package br.com.tavernaonline.chat;

import br.com.tavernaonline.chat.dto.EnviarMensagemRequest;
import br.com.tavernaonline.chat.dto.MensagemResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWsController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sessao/{sessaoId}/chat")
    public void enviar(
            @DestinationVariable UUID sessaoId,
            @Payload EnviarMensagemRequest request,
            Principal principal) {
        MensagemResponse resposta = chatService.registrarTexto(sessaoId, principal.getName(), request.conteudo());
        messagingTemplate.convertAndSend("/topic/sessao." + sessaoId + ".chat", resposta);
    }
}
