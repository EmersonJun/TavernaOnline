package br.com.tavernaonline.sessao;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.sessao.dto.CriarSessaoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;
    @Mock
    private CampanhaService campanhaService;
    @Mock
    private SessaoMapper sessaoMapper;
    @Mock
    private UsuarioAtual usuarioAtual;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    void criarDeveNumerarSequencialmente() {
        UUID campanhaId = UUID.randomUUID();
        Campanha campanha = new Campanha("Mina", null, "D&D 5e", "ABCD2345");
        campanha.setId(campanhaId);

        when(usuarioAtual.getId()).thenReturn(UUID.randomUUID());
        when(campanhaService.buscar(campanhaId)).thenReturn(campanha);
        when(sessaoRepository.countByCampanhaId(campanhaId)).thenReturn(2);
        lenient().when(sessaoMapper.toResponse(org.mockito.ArgumentMatchers.any())).thenReturn(null);

        sessaoService.criar(campanhaId, new CriarSessaoRequest("A cidade sitiada"));

        ArgumentCaptor<Sessao> captor = ArgumentCaptor.forClass(Sessao.class);
        org.mockito.Mockito.verify(sessaoRepository).save(captor.capture());
        assertThat(captor.getValue().getNumero()).isEqualTo(3);
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusSessao.AGENDADA);
    }

    @Test
    void iniciarSessaoEncerradaDeveFalhar() {
        UUID sessaoId = UUID.randomUUID();
        Campanha campanha = new Campanha("Mina", null, "D&D 5e", "ABCD2345");
        campanha.setId(UUID.randomUUID());
        Sessao sessao = new Sessao(campanha, 1, "Final");
        sessao.setId(sessaoId);
        sessao.setStatus(StatusSessao.ENCERRADA);

        when(usuarioAtual.getId()).thenReturn(UUID.randomUUID());
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> sessaoService.iniciar(sessaoId))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void encerrarDeveDesligarCombate() {
        UUID sessaoId = UUID.randomUUID();
        Campanha campanha = new Campanha("Mina", null, "D&D 5e", "ABCD2345");
        campanha.setId(UUID.randomUUID());
        Sessao sessao = new Sessao(campanha, 1, "Final");
        sessao.setId(sessaoId);
        sessao.setStatus(StatusSessao.EM_ANDAMENTO);
        sessao.setCombateAtivo(true);

        when(usuarioAtual.getId()).thenReturn(UUID.randomUUID());
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        lenient().when(sessaoMapper.toResponse(sessao)).thenReturn(null);

        sessaoService.encerrar(sessaoId);

        assertThat(sessao.getStatus()).isEqualTo(StatusSessao.ENCERRADA);
        assertThat(sessao.isCombateAtivo()).isFalse();
        assertThat(sessao.getEncerradaEm()).isNotNull();
    }
}
