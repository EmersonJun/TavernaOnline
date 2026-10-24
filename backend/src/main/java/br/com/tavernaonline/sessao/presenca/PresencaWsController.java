package br.com.tavernaonline.sessao.presenca;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class PresencaWsController {

    private final PresencaService presencaService;
    private final SessaoService sessaoService;
    private final CampanhaService campanhaService;
    private final UsuarioRepository usuarioRepository;

    public PresencaWsController(
            PresencaService presencaService,
            SessaoService sessaoService,
            CampanhaService campanhaService,
            UsuarioRepository usuarioRepository) {
        this.presencaService = presencaService;
        this.sessaoService = sessaoService;
        this.campanhaService = campanhaService;
        this.usuarioRepository = usuarioRepository;
    }

    @MessageMapping("/sessao/{sessaoId}/entrar")
    public void entrar(
            @DestinationVariable UUID sessaoId,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElseThrow();
        campanhaService.exigirParticipante(sessao.getCampanha().getId(), usuario.getId());
        presencaService.entrar(sessaoId, headerAccessor.getSessionId(), usuario.getNome());
    }
}
