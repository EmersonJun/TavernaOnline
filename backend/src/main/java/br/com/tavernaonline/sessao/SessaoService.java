package br.com.tavernaonline.sessao;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.sessao.dto.CriarSessaoRequest;
import br.com.tavernaonline.sessao.dto.SessaoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final CampanhaService campanhaService;
    private final SessaoMapper sessaoMapper;
    private final UsuarioAtual usuarioAtual;

    public SessaoService(
            SessaoRepository sessaoRepository,
            CampanhaService campanhaService,
            SessaoMapper sessaoMapper,
            UsuarioAtual usuarioAtual) {
        this.sessaoRepository = sessaoRepository;
        this.campanhaService = campanhaService;
        this.sessaoMapper = sessaoMapper;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public SessaoResponse criar(UUID campanhaId, CriarSessaoRequest request) {
        campanhaService.exigirMestre(campanhaId, usuarioAtual.getId());
        Campanha campanha = campanhaService.buscar(campanhaId);
        int numero = sessaoRepository.countByCampanhaId(campanhaId) + 1;
        Sessao sessao = new Sessao(campanha, numero, request.titulo());
        sessaoRepository.save(sessao);
        return sessaoMapper.toResponse(sessao);
    }

    @Transactional(readOnly = true)
    public List<SessaoResponse> listarPorCampanha(UUID campanhaId) {
        campanhaService.exigirParticipante(campanhaId, usuarioAtual.getId());
        return sessaoRepository.findByCampanhaIdOrderByNumeroAsc(campanhaId).stream()
                .map(sessaoMapper::toResponse)
                .toList();
    }

    @Transactional
    public SessaoResponse iniciar(UUID sessaoId) {
        Sessao sessao = carregarComoMestre(sessaoId);
        if (sessao.getStatus() == StatusSessao.ENCERRADA) {
            throw new RegraNegocioException("Uma sessao encerrada nao pode ser reiniciada");
        }
        sessao.setStatus(StatusSessao.EM_ANDAMENTO);
        sessao.setIniciadaEm(LocalDateTime.now());
        return sessaoMapper.toResponse(sessao);
    }

    @Transactional
    public SessaoResponse encerrar(UUID sessaoId) {
        Sessao sessao = carregarComoMestre(sessaoId);
        sessao.setStatus(StatusSessao.ENCERRADA);
        sessao.setEncerradaEm(LocalDateTime.now());
        sessao.setCombateAtivo(false);
        return sessaoMapper.toResponse(sessao);
    }

    @Transactional(readOnly = true)
    public Sessao buscar(UUID sessaoId) {
        return sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao nao encontrada"));
    }

    private Sessao carregarComoMestre(UUID sessaoId) {
        Sessao sessao = buscar(sessaoId);
        campanhaService.exigirMestre(sessao.getCampanha().getId(), usuarioAtual.getId());
        return sessao;
    }
}
