package br.com.tavernaonline.campanha;

import br.com.tavernaonline.campanha.dto.EntrarCampanhaRequest;
import br.com.tavernaonline.common.AcessoNegadoException;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.common.RegraNegocioException;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampanhaServiceTest {

    @Mock
    private CampanhaRepository campanhaRepository;
    @Mock
    private ParticipacaoRepository participacaoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private GeradorCodigoConvite geradorCodigoConvite;
    @Mock
    private CampanhaMapper campanhaMapper;
    @Mock
    private UsuarioAtual usuarioAtual;

    @InjectMocks
    private CampanhaService campanhaService;

    private Usuario usuarioComId(UUID id) {
        Usuario usuario = new Usuario("Fulano", "fulano@taverna.com", "hash");
        usuario.setId(id);
        return usuario;
    }

    @Test
    void entrarPorCodigoDeveSalvarParticipacaoComoJogador() {
        UUID usuarioId = UUID.randomUUID();
        Usuario jogador = usuarioComId(usuarioId);
        Campanha campanha = new Campanha("Mina Perdida", null, "D&D 5e", "ABCD2345");
        campanha.setId(UUID.randomUUID());

        when(usuarioAtual.getId()).thenReturn(usuarioId);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(jogador));
        when(campanhaRepository.findByCodigoConvite("ABCD2345")).thenReturn(Optional.of(campanha));
        when(participacaoRepository.existsByUsuarioIdAndCampanhaId(usuarioId, campanha.getId())).thenReturn(false);

        campanhaService.entrarPorCodigo(new EntrarCampanhaRequest("abcd2345"));

        ArgumentCaptor<Participacao> captor = ArgumentCaptor.forClass(Participacao.class);
        verify(participacaoRepository).save(captor.capture());
        assertThat(captor.getValue().getPapel()).isEqualTo(Papel.JOGADOR);
    }

    @Test
    void entrarPorCodigoDeveFalharQuandoCodigoNaoExiste() {
        when(usuarioAtual.getId()).thenReturn(UUID.randomUUID());
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuarioComId(UUID.randomUUID())));
        when(campanhaRepository.findByCodigoConvite(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> campanhaService.entrarPorCodigo(new EntrarCampanhaRequest("ZZZZ9999")))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(participacaoRepository, never()).save(any());
    }

    @Test
    void entrarPorCodigoDeveFalharQuandoJaParticipa() {
        UUID usuarioId = UUID.randomUUID();
        Campanha campanha = new Campanha("Mina Perdida", null, "D&D 5e", "ABCD2345");
        campanha.setId(UUID.randomUUID());

        when(usuarioAtual.getId()).thenReturn(usuarioId);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioComId(usuarioId)));
        when(campanhaRepository.findByCodigoConvite("ABCD2345")).thenReturn(Optional.of(campanha));
        when(participacaoRepository.existsByUsuarioIdAndCampanhaId(usuarioId, campanha.getId())).thenReturn(true);

        assertThatThrownBy(() -> campanhaService.entrarPorCodigo(new EntrarCampanhaRequest("ABCD2345")))
                .isInstanceOf(RegraNegocioException.class);

        verify(participacaoRepository, never()).save(any());
    }

    @Test
    void criarDeveGerarNovoCodigoQuandoHouverColisao() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioAtual.getId()).thenReturn(usuarioId);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioComId(usuarioId)));
        when(geradorCodigoConvite.gerar()).thenReturn("REPETIDO", "UNICO123");
        when(campanhaRepository.existsByCodigoConvite("REPETIDO")).thenReturn(true);
        when(campanhaRepository.existsByCodigoConvite("UNICO123")).thenReturn(false);
        lenient().when(campanhaMapper.toResponse(any(), any())).thenReturn(null);

        campanhaService.criar(new br.com.tavernaonline.campanha.dto.CriarCampanhaRequest("Nova", null, "Tormenta20"));

        ArgumentCaptor<Campanha> captor = ArgumentCaptor.forClass(Campanha.class);
        verify(campanhaRepository).save(captor.capture());
        assertThat(captor.getValue().getCodigoConvite()).isEqualTo("UNICO123");
    }

    @Test
    void exigirMestreDeveLancarQuandoUsuarioNaoEhMestre() {
        UUID campanhaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        when(participacaoRepository.existsByUsuarioIdAndCampanhaIdAndPapel(usuarioId, campanhaId, Papel.MESTRE))
                .thenReturn(false);

        assertThatThrownBy(() -> campanhaService.exigirMestre(campanhaId, usuarioId))
                .isInstanceOf(AcessoNegadoException.class);
    }
}
