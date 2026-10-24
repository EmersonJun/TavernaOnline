package br.com.tavernaonline.diario;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.AcessoNegadoException;
import br.com.tavernaonline.diario.dto.AnotacaoRequest;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnotacaoServiceTest {

    @Mock
    private AnotacaoRepository anotacaoRepository;
    @Mock
    private CampanhaService campanhaService;
    @Mock
    private SessaoService sessaoService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AnotacaoMapper anotacaoMapper;
    @Mock
    private UsuarioAtual usuarioAtual;

    @InjectMocks
    private AnotacaoService anotacaoService;

    @Test
    void jogadorNaoPodeCriarAnotacaoSomenteMestre() {
        UUID campanhaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        when(usuarioAtual.getId()).thenReturn(usuarioId);
        doThrow(new AcessoNegadoException("nao e mestre"))
                .when(campanhaService).exigirMestre(eq(campanhaId), eq(usuarioId));

        AnotacaoRequest request = new AnotacaoRequest(
                "Segredo", "Apenas o mestre ve", VisibilidadeAnotacao.SOMENTE_MESTRE, null);

        assertThatThrownBy(() -> anotacaoService.criar(campanhaId, request))
                .isInstanceOf(AcessoNegadoException.class);

        verify(anotacaoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void jogadorListaApenasAnotacoesPublicas() {
        UUID campanhaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        when(usuarioAtual.getId()).thenReturn(usuarioId);
        doThrow(new AcessoNegadoException("nao e mestre"))
                .when(campanhaService).exigirMestre(eq(campanhaId), eq(usuarioId));
        when(anotacaoRepository.findByCampanhaIdAndVisibilidadeOrderByCriadoEmDesc(
                campanhaId, VisibilidadeAnotacao.PUBLICA)).thenReturn(List.of());

        anotacaoService.listarPorCampanha(campanhaId);

        verify(anotacaoRepository)
                .findByCampanhaIdAndVisibilidadeOrderByCriadoEmDesc(campanhaId, VisibilidadeAnotacao.PUBLICA);
        verify(anotacaoRepository, never()).findByCampanhaIdOrderByCriadoEmDesc(org.mockito.ArgumentMatchers.any());
    }
}
