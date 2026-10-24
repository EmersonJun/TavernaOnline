package br.com.tavernaonline.chat;

import br.com.tavernaonline.common.EntidadeBase;
import br.com.tavernaonline.sessao.Sessao;
import br.com.tavernaonline.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mensagem_chat")
@Getter
@Setter
@NoArgsConstructor
public class MensagemChat extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMensagem tipo = TipoMensagem.TEXTO;

    @Column(nullable = false, length = 2000)
    private String conteudo;

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
