package br.com.tavernaonline.chat;

import br.com.tavernaonline.campanha.CampanhaService;
import br.com.tavernaonline.chat.dto.MensagemResponse;
import br.com.tavernaonline.common.RecursoNaoEncontradoException;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.sessao.SessaoService;
import br.com.tavernaonline.usuario.Usuario;
import br.com.tavernaonline.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final MensagemChatRepository mensagemRepository;
    private final SessaoService sessaoService;
    private final CampanhaService campanhaService;
    private final UsuarioRepository usuarioRepository;
    private final MensagemChatMapper mensagemMapper;

    public ChatService(
            MensagemChatRepository mensagemRepository,
            SessaoService sessaoService,
            CampanhaService campanhaService,
            UsuarioRepository usuarioRepository,
            MensagemChatMapper mensagemMapper) {
        this.mensagemRepository = mensagemRepository;
        this.sessaoService = sessaoService;
        this.campanhaService = campanhaService;
        this.usuarioRepository = usuarioRepository;
        this.mensagemMapper = mensagemMapper;
    }

    @Transactional
    public MensagemResponse registrarTexto(UUID sessaoId, String email, String conteudo) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        Usuario autor = buscarUsuario(email);
        campanhaService.exigirParticipante(sessao.getCampanha().getId(), autor.getId());

        MensagemChat mensagem = new MensagemChat();
        mensagem.setSessao(sessao);
        mensagem.setAutor(autor);
        mensagem.setTipo(TipoMensagem.TEXTO);
        mensagem.setConteudo(conteudo);
        mensagemRepository.save(mensagem);
        return mensagemMapper.toResponse(mensagem);
    }

    @Transactional
    public MensagemResponse registrarSistema(UUID sessaoId, String conteudo) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        MensagemChat mensagem = new MensagemChat();
        mensagem.setSessao(sessao);
        mensagem.setTipo(TipoMensagem.SISTEMA);
        mensagem.setConteudo(conteudo);
        mensagemRepository.save(mensagem);
        return mensagemMapper.toResponse(mensagem);
    }

    @Transactional(readOnly = true)
    public List<MensagemResponse> historico(UUID sessaoId, String email) {
        Sessao sessao = sessaoService.buscar(sessaoId);
        Usuario usuario = buscarUsuario(email);
        campanhaService.exigirParticipante(sessao.getCampanha().getId(), usuario.getId());
        return mensagemRepository.findBySessaoIdOrderByCriadoEmAsc(sessaoId).stream()
                .map(mensagemMapper::toResponse)
                .toList();
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
