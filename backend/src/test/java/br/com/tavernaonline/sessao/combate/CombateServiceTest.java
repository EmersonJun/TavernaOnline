package br.com.tavernaonline.sessao.combate;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.personagem.PersonagemRepository;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.sessao.combate.dto.CombatenteRequest;
import br.com.tavernaonline.sessao.combate.dto.CombatenteResponse;
import br.com.tavernaonline.sessao.combate.dto.IniciarCombateRequest;
import br.com.tavernaonline.campanha.Campanha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CombateServiceTest {

    @Mock
    private ParticipanteCombateRepository participanteRepository;
    @Mock
    private SessaoService sessaoService;
    @Mock
    private CampanhaService campanhaService;
    @Mock
    private PersonagemRepository personagemRepository;
    @Mock
    private CombateMapper combateMapper;
    @Mock
    private UsuarioAtual usuarioAtual;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private CombateService combateService;

    private Sessao sessao() {
        Campanha campanha = new Campanha("Mina", null, "D&D 5e", "ABCD2345");
        campanha.setId(UUID.randomUUID());
        Sessao sessao = new Sessao(campanha, 1, "Combate");
        sessao.setId(UUID.randomUUID());
        return sessao;
    }

    @Test
    void iniciarDeveOrdenarPorIniciativaDecrescente() {
        Sessao sessao = sessao();
        when(usuarioAtual.getId()).thenReturn(UUID.randomUUID());
        when(sessaoService.buscar(sessao.getId())).thenReturn(sessao);
        lenient().when(combateMapper.toResponse(any()))
                .thenReturn(new CombatenteResponse(UUID.randomUUID(), "x", 0, 0, null));

        IniciarCombateRequest request = new IniciarCombateRequest(List.of(
                new CombatenteRequest("Goblin", 12, null),
                new CombatenteRequest("Thalia", 18, null),
                new CombatenteRequest("Orc", 15, null)));

        combateService.iniciar(sessao.getId(), request);

        ArgumentCaptor<ParticipanteCombate> captor = ArgumentCaptor.forClass(ParticipanteCombate.class);
        verify(participanteRepository, org.mockito.Mockito.times(3)).save(captor.capture());
        List<ParticipanteCombate> salvos = captor.getAllValues();

        assertThat(salvos).extracting(ParticipanteCombate::getNome)
                .containsExactly("Thalia", "Orc", "Goblin");
        assertThat(salvos).extracting(ParticipanteCombate::getOrdem)
                .containsExactly(0, 1, 2);
        assertThat(sessao.isCombateAtivo()).isTrue();
        assertThat(sessao.getRodadaAtual()).isEqualTo(1);
    }

    @Test
    void proximoTurnoDeveAvancarDentroDaRodada() {
        Sessao sessao = sessao();
        sessao.setCombateAtivo(true);
        sessao.setRodadaAtual(1);
        sessao.setIndiceTurnoAtual(0);
        prepararTurno(sessao);

        combateService.proximoTurno(sessao.getId());

        assertThat(sessao.getIndiceTurnoAtual()).isEqualTo(1);
        assertThat(sessao.getRodadaAtual()).isEqualTo(1);
    }

    @Test
    void proximoTurnoDeveVoltarAoInicioEIncrementarRodada() {
        Sessao sessao = sessao();
        sessao.setCombateAtivo(true);
        sessao.setRodadaAtual(1);
        sessao.setIndiceTurnoAtual(2);
        prepararTurno(sessao);

        combateService.proximoTurno(sessao.getId());

        assertThat(sessao.getIndiceTurnoAtual()).isZero();
        assertThat(sessao.getRodadaAtual()).isEqualTo(2);
    }

    private void prepararTurno(Sessao sessao) {
        when(usuarioAtual.getId()).thenReturn(UUID.randomUUID());
        when(sessaoService.buscar(sessao.getId())).thenReturn(sessao);
        when(participanteRepository.findBySessaoIdOrderByOrdemAsc(sessao.getId()))
                .thenReturn(List.of(new ParticipanteCombate(), new ParticipanteCombate(), new ParticipanteCombate()));
        lenient().when(combateMapper.toResponse(any()))
                .thenReturn(new CombatenteResponse(UUID.randomUUID(), "x", 0, 0, null));
    }
}
