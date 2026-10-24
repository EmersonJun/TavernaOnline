package br.com.tavernaonline.chat;

import br.com.tavernaonline.chat.dto.MensagemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MensagemChatMapper {

    @Mapping(target = "sessaoId", source = "sessao.id")
    @Mapping(target = "autorId", source = "autor.id")
    @Mapping(target = "autorNome", source = "autor.nome")
    MensagemResponse toResponse(MensagemChat mensagem);
}
