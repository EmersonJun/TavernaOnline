package br.com.tavernaonline.sessao.combate;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.personagem.Personagem;
import br.com.tavernaonline.personagem.PersonagemRepository;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.sessao.combate.dto.CombateResponse;
import br.com.tavernaonline.sessao.combate.dto.CombatenteRequest;
import br.com.tavernaonline.sessao.combate.dto.CombatenteResponse;
import br.com.tavernaonline.sessao.combate.dto.IniciarCombateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CombateService {

    private final ParticipanteCombateRepository participanteRepository;
    private final SessaoService sessaoService;
    private final CampanhaService campanhaService;
    private final PersonagemRepository personagemRepository;
    private final CombateMapper combateMapper;
    private final UsuarioAtual usuarioAtual;
    private final SimpMessagingTemplate messagingTemplate;

    public CombateService(
            ParticipanteCombateRepository participanteRepository,
            SessaoService sessaoService,
            CampanhaService campanhaService,
            PersonagemRepository personagemRepository,
            CombateMapper combateMapper,
            UsuarioAtual usuarioAtual,
            SimpMessagingTemplate messagingTemplate) {
        this.participanteRepository = participanteRepository;
        this.sessaoService = sessaoService;
        this.campanhaService = campanhaService;
        this.personagemRepository = personagemRepository;
        this.combateMapper = combateMapper;
        this.usuarioAtual = usuarioAtual;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public CombateResponse iniciar(UUID sessaoId, IniciarCombateRequest request) {
        Sessao sessao = carregarComoMestre(sessaoId);
        participanteRepository.deleteBySessaoId(sessaoId);

        List<CombatenteRequest> ordenados = request.combatentes().stream()
                .sorted(Comparator.comparingInt(CombatenteRequest::iniciativa).reversed()
                        .thenComparing(CombatenteRequest::nome))
                .toList();

        int ordem = 0;
        for (CombatenteRequest combatente : ordenados) {
            ParticipanteCombate participante = new ParticipanteCombate();
            participante.setSessao(sessao);
            participante.setNome(combatente.nome());
            participante.setIniciativa(combatente.iniciativa());
            participante.setOrdem(ordem++);
            participante.setPersonagem(resolverPersonagem(sessao.getCampanha().getId(), combatente.personagemId()));
            participanteRepository.save(participante);
        }

        sessao.setCombateAtivo(true);
        sessao.setRodadaAtual(1);
        sessao.setIndiceTurnoAtual(0);
        return publicar(sessao);
    }

    @Transactional
    public CombateResponse proximoTurno(UUID sessaoId) {
        Sessao sessao = carregarComoMestre(sessaoId);
        if (!sessao.isCombateAtivo()) {
            throw new RegraNegocioException("Nao ha combate ativo nesta sessao");
        }
        int total = participanteRepository.findBySessaoIdOrderByOrdemAsc(sessaoId).size();
        if (total == 0) {
            throw new RegraNegocioException("Nao ha combatentes no combate");
        }

        int proximo = sessao.getIndiceTurnoAtual() + 1;
        if (proximo >= total) {
            proximo = 0;
            sessao.setRodadaAtual(sessao.getRodadaAtual() + 1);
        }
        sessao.setIndiceTurnoAtual(proximo);
        return publicar(sessao);
    }

    @Transactional
    public CombateResponse encerrar(UUID sessaoId) {
        Sessao sessao = carregarComoMestre(sessaoId);
        participanteRepository.deleteBySessaoId(sessaoId);
        sessao.setCombateAtivo(false);
        sessao.setRodadaAtual(0);
        sessao.setIndiceTurnoAtual(0);
        return publicar(sessao);
    }

    @Transactional(readOnly = true)
    public CombateResponse estado(UUID sessaoId) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        campanhaService.exigirParticipante(sessao.getCampanha().getId(), usuarioAtual.getId());
        return montar(sessao);
    }

    private CombateResponse publicar(Sessao sessao) {
        CombateResponse resposta = montar(sessao);
        messagingTemplate.convertAndSend("/topic/sessao." + sessao.getId() + ".combate", resposta);
        return resposta;
    }

    private CombateResponse montar(Sessao sessao) {
        List<CombatenteResponse> ordem = participanteRepository
                .findBySessaoIdOrderByOrdemAsc(sessao.getId()).stream()
                .map(combateMapper::toResponse)
                .toList();

        UUID participanteDaVez = null;
        if (sessao.isCombateAtivo() && !ordem.isEmpty()
                && sessao.getIndiceTurnoAtual() < ordem.size()) {
            participanteDaVez = ordem.get(sessao.getIndiceTurnoAtual()).id();
        }

        return new CombateResponse(
                sessao.getId(),
                sessao.isCombateAtivo(),
                sessao.getRodadaAtual(),
                sessao.getIndiceTurnoAtual(),
                participanteDaVez,
                ordem);
    }

    private Personagem resolverPersonagem(UUID campanhaId, UUID personagemId) {
        if (personagemId == null) {
            return null;
        }
        Personagem personagem = personagemRepository.findById(personagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Personagem nao encontrado"));
        if (!personagem.getCampanha().getId().equals(campanhaId)) {
            throw new RegraNegocioException("O personagem nao pertence a esta campanha");
        }
        return personagem;
    }

    private Sessao carregarComoMestre(UUID sessaoId) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        campanhaService.exigirMestre(sessao.getCampanha().getId(), usuarioAtual.getId());
        return sessao;
    }
}
