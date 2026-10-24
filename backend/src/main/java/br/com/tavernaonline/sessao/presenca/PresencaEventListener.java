package br.com.tavernaonline.sessao.presenca;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class PresencaEventListener {

    private final PresencaService presencaService;

    public PresencaEventListener(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    @EventListener
    public void aoDesconectar(SessionDisconnectEvent evento) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(evento.getMessage());
        String conexaoId = accessor.getSessionId();
        if (conexaoId != null) {
            presencaService.sair(conexaoId);
        }
    }
}
