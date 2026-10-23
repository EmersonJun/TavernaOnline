package br.com.tavernaonline.personagem;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.AcessoNegadoException;
import br.com.tavernaonline.personagem.dto.AtributosDto;
import br.com.tavernaonline.personagem.dto.PersonagemRequest;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonagemServiceTest {

    @Mock
    private PersonagemRepository personagemRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CampanhaService campanhaService;
    @Mock
    private PersonagemMapper personagemMapper;
    @Mock
    private UsuarioAtual usuarioAtual;

    @InjectMocks
    private PersonagemService personagemService;

    private PersonagemRequest requestValido() {
        return new PersonagemRequest("Thalia", "Guerreira", "Humana", 1, 10, 10, 15,
                new AtributosDto(16, 12, 14, 10, 11, 8), List.of(), List.of());
    }

    private Personagem personagemDe(UUID donoId, UUID campanhaId) {
        Campanha campanha = new Campanha("Mina", null, "D&D 5e", "ABCD2345");
        campanha.setId(campanhaId);
        Usuario dono = new Usuario("Dono", "dono@taverna.com", "hash");
        dono.setId(donoId);
        Personagem personagem = new Personagem();
        personagem.setId(UUID.randomUUID());
        personagem.setCampanha(campanha);
        personagem.setDono(dono);
        return personagem;
    }

    @Test
    void atualizarDeveFalharQuandoUsuarioNaoEhDonoNemMestre() {
        UUID donoId = UUID.randomUUID();
        UUID campanhaId = UUID.randomUUID();
        UUID estranhoId = UUID.randomUUID();
        Personagem personagem = personagemDe(donoId, campanhaId);

        when(personagemRepository.findById(personagem.getId())).thenReturn(Optional.of(personagem));
        when(usuarioAtual.getId()).thenReturn(estranhoId);
        doThrow(new AcessoNegadoException("nao e mestre"))
                .when(campanhaService).exigirMestre(eq(campanhaId), eq(estranhoId));

        assertThatThrownBy(() -> personagemService.atualizar(personagem.getId(), requestValido()))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void criarDevePropagarQuandoUsuarioNaoParticipaDaCampanha() {
        UUID campanhaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        when(usuarioAtual.getId()).thenReturn(usuarioId);
        doThrow(new AcessoNegadoException("nao participa"))
                .when(campanhaService).exigirParticipante(campanhaId, usuarioId);

        assertThatThrownBy(() -> personagemService.criar(campanhaId, requestValido()))
                .isInstanceOf(AcessoNegadoException.class);

        verify(personagemRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
