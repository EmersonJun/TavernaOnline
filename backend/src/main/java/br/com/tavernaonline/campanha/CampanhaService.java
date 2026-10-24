package br.com.tavernaonline.campanha;

import br.com.tavernaonline.campanha.dto.CampanhaResponse;
import br.com.tavernaonline.campanha.dto.CriarCampanhaRequest;
import br.com.tavernaonline.campanha.dto.EntrarCampanhaRequest;
import br.com.tavernaonline.campanha.dto.ParticipanteResponse;
import br.com.tavernaonline.common.AcessoNegadoException;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CampanhaService {

    private final CampanhaRepository campanhaRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeradorCodigoConvite geradorCodigoConvite;
    private final CampanhaMapper campanhaMapper;
    private final UsuarioAtual usuarioAtual;

    public CampanhaService(
            CampanhaRepository campanhaRepository,
            ParticipacaoRepository participacaoRepository,
            UsuarioRepository usuarioRepository,
            GeradorCodigoConvite geradorCodigoConvite,
            CampanhaMapper campanhaMapper,
            UsuarioAtual usuarioAtual) {
        this.campanhaRepository = campanhaRepository;
        this.participacaoRepository = participacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.geradorCodigoConvite = geradorCodigoConvite;
        this.campanhaMapper = campanhaMapper;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public CampanhaResponse criar(CriarCampanhaRequest request) {
        Usuario mestre = usuarioLogado();
        Campanha campanha = new Campanha(request.nome(), request.descricao(), request.sistema(), gerarCodigoUnico());
        campanhaRepository.save(campanha);
        participacaoRepository.save(new Participacao(mestre, campanha, Papel.MESTRE));
        return campanhaMapper.toResponse(campanha, Papel.MESTRE);
    }

    @Transactional(readOnly = true)
    public List<CampanhaResponse> listarMinhas() {
        UUID usuarioId = usuarioAtual.getId();
        return participacaoRepository.findByUsuarioId(usuarioId).stream()
                .map(p -> campanhaMapper.toResponse(p.getCampanha(), p.getPapel()))
                .toList();
    }

    @Transactional
    public CampanhaResponse entrarPorCodigo(EntrarCampanhaRequest request) {
        Usuario jogador = usuarioLogado();
        Campanha campanha = campanhaRepository.findByCodigoConvite(request.codigoConvite().toUpperCase())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Codigo de convite invalido"));
        if (!campanha.isAtiva()) {
            throw new RegraNegocioException("Esta campanha nao esta aceitando novos jogadores");
        }
        if (participacaoRepository.existsByUsuarioIdAndCampanhaId(jogador.getId(), campanha.getId())) {
            throw new RegraNegocioException("Voce ja participa desta campanha");
        }
        participacaoRepository.save(new Participacao(jogador, campanha, Papel.JOGADOR));
        return campanhaMapper.toResponse(campanha, Papel.JOGADOR);
    }

    @Transactional(readOnly = true)
    public List<ParticipanteResponse> listarParticipantes(UUID campanhaId) {
        exigirParticipante(campanhaId, usuarioAtual.getId());
        return participacaoRepository.findByCampanhaId(campanhaId).stream()
                .map(campanhaMapper::toParticipante)
                .toList();
    }

    @Transactional(readOnly = true)
    public Campanha buscar(UUID campanhaId) {
        return campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campanha nao encontrada"));
    }

    @Transactional(readOnly = true)
    public List<String> emailsDosMestres(UUID campanhaId) {
        return participacaoRepository.findByCampanhaIdAndPapel(campanhaId, Papel.MESTRE).stream()
                .map(p -> p.getUsuario().getEmail())
                .toList();
    }

    public void exigirParticipante(UUID campanhaId, UUID usuarioId) {
        if (!participacaoRepository.existsByUsuarioIdAndCampanhaId(usuarioId, campanhaId)) {
            throw new AcessoNegadoException("Voce nao participa desta campanha");
        }
    }

    public void exigirMestre(UUID campanhaId, UUID usuarioId) {
        if (!participacaoRepository.existsByUsuarioIdAndCampanhaIdAndPapel(usuarioId, campanhaId, Papel.MESTRE)) {
            throw new AcessoNegadoException("Apenas o mestre da campanha pode fazer isso");
        }
    }

    private String gerarCodigoUnico() {
        String codigo;
        do {
            codigo = geradorCodigoConvite.gerar();
        } while (campanhaRepository.existsByCodigoConvite(codigo));
        return codigo;
    }

    private Usuario usuarioLogado() {
        return usuarioRepository.findById(usuarioAtual.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
