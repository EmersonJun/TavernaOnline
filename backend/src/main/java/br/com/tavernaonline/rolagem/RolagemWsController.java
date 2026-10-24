package br.com.tavernaonline.rolagem;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.rolagem.dto.RolagemResponse;
import br.com.tavernaonline.rolagem.dto.RolarRequest;
import br.com.tavernaonline.sessao.SessaoService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class RolagemWsController {

    private final RolagemService rolagemService;
    private final SessaoService sessaoService;
    private final CampanhaService campanhaService;
    private final SimpMessagingTemplate messagingTemplate;

    public RolagemWsController(
            RolagemService rolagemService,
            SessaoService sessaoService,
            CampanhaService campanhaService,
            SimpMessagingTemplate messagingTemplate) {
        this.rolagemService = rolagemService;
        this.sessaoService = sessaoService;
        this.campanhaService = campanhaService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sessao/{sessaoId}/rolagem")
    public void rolar(
            @DestinationVariable UUID sessaoId,
            @Payload RolarRequest request,
            Principal principal) {
        RolagemResponse resposta = rolagemService.rolar(sessaoId, principal.getName(), request);

        if (!resposta.secreta()) {
            messagingTemplate.convertAndSend("/topic/sessao." + sessaoId + ".rolagem", resposta);
            return;
        }

        UUID campanhaId = sessaoService.buscar(sessaoId).getCampanha().getId();
        for (String mestre : campanhaService.emailsDosMestres(campanhaId)) {
            messagingTemplate.convertAndSendToUser(mestre, "/queue/sessao." + sessaoId + ".rolagem", resposta);
        }
    }
}
