package br.com.tavernaonline.rolagem;

import br.com.tavernaonline.rolagem.dto.RolagemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolagemMapper {

    @Mapping(target = "sessaoId", source = "sessao.id")
    @Mapping(target = "autorId", source = "autor.id")
    @Mapping(target = "autorNome", source = "autor.nome")
    @Mapping(target = "personagemId", source = "personagem.id")
    RolagemResponse toResponse(Rolagem rolagem);
}
