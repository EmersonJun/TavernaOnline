package br.com.tavernaonline.diario;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.AcessoNegadoException;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.diario.dto.AnotacaoRequest;
import br.com.tavernaonline.diario.dto.AnotacaoResponse;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AnotacaoService {

    private final AnotacaoRepository anotacaoRepository;
    private final CampanhaService campanhaService;
    private final SessaoService sessaoService;
    private final UsuarioRepository usuarioRepository;
    private final AnotacaoMapper anotacaoMapper;
    private final UsuarioAtual usuarioAtual;

    public AnotacaoService(
            AnotacaoRepository anotacaoRepository,
            CampanhaService campanhaService,
            SessaoService sessaoService,
            UsuarioRepository usuarioRepository,
            AnotacaoMapper anotacaoMapper,
            UsuarioAtual usuarioAtual) {
        this.anotacaoRepository = anotacaoRepository;
        this.campanhaService = campanhaService;
        this.sessaoService = sessaoService;
        this.usuarioRepository = usuarioRepository;
        this.anotacaoMapper = anotacaoMapper;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public AnotacaoResponse criar(UUID campanhaId, AnotacaoRequest request) {
        UUID usuarioId = usuarioAtual.getId();
        campanhaService.exigirParticipante(campanhaId, usuarioId);
        if (request.visibilidade() == VisibilidadeAnotacao.SOMENTE_MESTRE) {
            campanhaService.exigirMestre(campanhaId, usuarioId);
        }

        Campanha campanha = campanhaService.buscar(campanhaId);
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        Anotacao anotacao = new Anotacao();
        anotacao.setCampanha(campanha);
        anotacao.setAutor(autor);
        anotacao.setSessao(resolverSessao(campanhaId, request.sessaoId()));
        anotacao.setTitulo(request.titulo());
        anotacao.setConteudo(request.conteudo());
        anotacao.setVisibilidade(request.visibilidade());

        anotacaoRepository.save(anotacao);
        return anotacaoMapper.toResponse(anotacao);
    }

    @Transactional(readOnly = true)
    public List<AnotacaoResponse> listarPorCampanha(UUID campanhaId) {
        UUID usuarioId = usuarioAtual.getId();
        campanhaService.exigirParticipante(campanhaId, usuarioId);

        List<Anotacao> anotacoes = ehMestre(campanhaId, usuarioId)
                ? anotacaoRepository.findByCampanhaIdOrderByCriadoEmDesc(campanhaId)
                : anotacaoRepository.findByCampanhaIdAndVisibilidadeOrderByCriadoEmDesc(
                        campanhaId, VisibilidadeAnotacao.PUBLICA);

        return anotacoes.stream().map(anotacaoMapper::toResponse).toList();
    }

    @Transactional
    public AnotacaoResponse atualizar(UUID anotacaoId, AnotacaoRequest request) {
        Anotacao anotacao = carregar(anotacaoId);
        UUID campanhaId = anotacao.getCampanha().getId();
        exigirAutorOuMestre(anotacao);
        if (request.visibilidade() == VisibilidadeAnotacao.SOMENTE_MESTRE
                && !ehMestre(campanhaId, usuarioAtual.getId())) {
            throw new AcessoNegadoException("Apenas o mestre pode tornar uma anotacao privada");
        }

        anotacao.setTitulo(request.titulo());
        anotacao.setConteudo(request.conteudo());
        anotacao.setVisibilidade(request.visibilidade());
        anotacao.setSessao(resolverSessao(campanhaId, request.sessaoId()));
        return anotacaoMapper.toResponse(anotacao);
    }

    @Transactional
    public void excluir(UUID anotacaoId) {
        Anotacao anotacao = carregar(anotacaoId);
        exigirAutorOuMestre(anotacao);
        anotacaoRepository.delete(anotacao);
    }

    private Sessao resolverSessao(UUID campanhaId, UUID sessaoId) {
        if (sessaoId == null) {
            return null;
        }
        Sessao sessao = sessaoService.buscar(sessaoId);
        if (!sessao.getCampanha().getId().equals(campanhaId)) {
            throw new RegraNegocioException("A sessao informada nao pertence a esta campanha");
        }
        return sessao;
    }

    private void exigirAutorOuMestre(Anotacao anotacao) {
        UUID usuarioId = usuarioAtual.getId();
        if (anotacao.getAutor().getId().equals(usuarioId)) {
            return;
        }
        if (!ehMestre(anotacao.getCampanha().getId(), usuarioId)) {
            throw new AcessoNegadoException("Apenas o autor ou o mestre podem alterar esta anotacao");
        }
    }

    private boolean ehMestre(UUID campanhaId, UUID usuarioId) {
        try {
            campanhaService.exigirMestre(campanhaId, usuarioId);
            return true;
        } catch (AcessoNegadoException ex) {
            return false;
        }
    }

    private Anotacao carregar(UUID anotacaoId) {
        return anotacaoRepository.findById(anotacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Anotacao nao encontrada"));
    }
}
