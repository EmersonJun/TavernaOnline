package br.com.tavernaonline.rolagem;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.personagem.Personagem;
import br.com.tavernaonline.personagem.PersonagemRepository;
import br.com.tavernaonline.rolagem.dto.RolagemResponse;
import br.com.tavernaonline.rolagem.dto.RolarRequest;
import br.com.tavernaonline.rolagem.motor.DiceRoller;
import br.com.tavernaonline.rolagem.motor.ResultadoRolagem;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RolagemService {

    private final RolagemRepository rolagemRepository;
    private final DiceRoller diceRoller;
    private final SessaoService sessaoService;
    private final CampanhaService campanhaService;
    private final UsuarioRepository usuarioRepository;
    private final PersonagemRepository personagemRepository;
    private final RolagemMapper rolagemMapper;

    public RolagemService(
            RolagemRepository rolagemRepository,
            DiceRoller diceRoller,
            SessaoService sessaoService,
            CampanhaService campanhaService,
            UsuarioRepository usuarioRepository,
            PersonagemRepository personagemRepository,
            RolagemMapper rolagemMapper) {
        this.rolagemRepository = rolagemRepository;
        this.diceRoller = diceRoller;
        this.sessaoService = sessaoService;
        this.campanhaService = campanhaService;
        this.usuarioRepository = usuarioRepository;
        this.personagemRepository = personagemRepository;
        this.rolagemMapper = rolagemMapper;
    }

    @Transactional
    public RolagemResponse rolar(UUID sessaoId, String email, RolarRequest request) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        UUID campanhaId = sessao.getCampanha().getId();
        Usuario autor = buscarUsuario(email);
        campanhaService.exigirParticipante(campanhaId, autor.getId());

        if (request.secreta()) {
            campanhaService.exigirMestre(campanhaId, autor.getId());
        }

        ResultadoRolagem resultado = diceRoller.rolar(request.notacao());

        Rolagem rolagem = new Rolagem();
        rolagem.setSessao(sessao);
        rolagem.setAutor(autor);
        rolagem.setPersonagem(resolverPersonagem(campanhaId, request.personagemId()));
        rolagem.setNotacao(resultado.notacao());
        rolagem.setResultados(resultado.resultados());
        rolagem.setModificador(resultado.modificador());
        rolagem.setTotal(resultado.total());
        rolagem.setMotivo(request.motivo());
        rolagem.setSecreta(request.secreta());

        rolagemRepository.save(rolagem);
        return rolagemMapper.toResponse(rolagem);
    }

    @Transactional(readOnly = true)
    public List<RolagemResponse> historicoPublico(UUID sessaoId, String email) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        Usuario usuario = buscarUsuario(email);
        campanhaService.exigirParticipante(sessao.getCampanha().getId(), usuario.getId());
        return rolagemRepository.findBySessaoIdAndSecretaFalseOrderByCriadoEmAsc(sessaoId).stream()
                .map(rolagemMapper::toResponse)
                .toList();
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

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
