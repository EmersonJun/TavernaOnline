package br.com.tavernaonline.personagem;

import br.com.tavernaonline.campanha.Campanha;
import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.common.AcessoNegadoException;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.personagem.dto.PersonagemRequest;
import br.com.tavernaonline.personagem.dto.PersonagemResponse;
import br.com.tavernaonline.security.UsuarioAtual;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PersonagemService {

    private final PersonagemRepository personagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final CampanhaService campanhaService;
    private final PersonagemMapper personagemMapper;
    private final UsuarioAtual usuarioAtual;

    public PersonagemService(
            PersonagemRepository personagemRepository,
            UsuarioRepository usuarioRepository,
            CampanhaService campanhaService,
            PersonagemMapper personagemMapper,
            UsuarioAtual usuarioAtual) {
        this.personagemRepository = personagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.campanhaService = campanhaService;
        this.personagemMapper = personagemMapper;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public PersonagemResponse criar(UUID campanhaId, PersonagemRequest request) {
        UUID usuarioId = usuarioAtual.getId();
        campanhaService.exigirParticipante(campanhaId, usuarioId);
        Campanha campanha = campanhaService.buscar(campanhaId);
        Usuario dono = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        Personagem personagem = new Personagem();
        personagem.setCampanha(campanha);
        personagem.setDono(dono);
        aplicar(request, personagem);
        personagemRepository.save(personagem);
        return personagemMapper.toResponse(personagem);
    }

    @Transactional(readOnly = true)
    public List<PersonagemResponse> listarPorCampanha(UUID campanhaId) {
        campanhaService.exigirParticipante(campanhaId, usuarioAtual.getId());
        return personagemRepository.findByCampanhaId(campanhaId).stream()
                .map(personagemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PersonagemResponse buscar(UUID personagemId) {
        Personagem personagem = carregar(personagemId);
        campanhaService.exigirParticipante(personagem.getCampanha().getId(), usuarioAtual.getId());
        return personagemMapper.toResponse(personagem);
    }

    @Transactional
    public PersonagemResponse atualizar(UUID personagemId, PersonagemRequest request) {
        Personagem personagem = carregar(personagemId);
        exigirDonoOuMestre(personagem);
        aplicar(request, personagem);
        return personagemMapper.toResponse(personagem);
    }

    @Transactional
    public void excluir(UUID personagemId) {
        Personagem personagem = carregar(personagemId);
        exigirDonoOuMestre(personagem);
        personagemRepository.delete(personagem);
    }

    private void aplicar(PersonagemRequest request, Personagem personagem) {
        personagem.setNome(request.nome());
        personagem.setClasse(request.classe());
        personagem.setRaca(request.raca());
        personagem.setNivel(request.nivel());
        personagem.setPvAtual(request.pvAtual());
        personagem.setPvMaximo(request.pvMaximo());
        personagem.setClasseArmadura(request.classeArmadura());
        personagem.setAtributos(personagemMapper.toAtributos(request.atributos()));

        personagem.getPericias().clear();
        if (request.pericias() != null) {
            personagem.getPericias().addAll(personagemMapper.toPericias(request.pericias()));
        }

        personagem.getInventario().clear();
        if (request.inventario() != null) {
            personagem.getInventario().addAll(personagemMapper.toInventario(request.inventario()));
        }
    }

    private void exigirDonoOuMestre(Personagem personagem) {
        UUID usuarioId = usuarioAtual.getId();
        if (personagem.getDono().getId().equals(usuarioId)) {
            return;
        }
        if (!ehMestre(personagem.getCampanha().getId(), usuarioId)) {
            throw new AcessoNegadoException("Apenas o dono do personagem ou o mestre podem alterar a ficha");
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

    private Personagem carregar(UUID personagemId) {
        return personagemRepository.findById(personagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Personagem nao encontrado"));
    }
}
