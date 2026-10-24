package br.com.tavernaonline.diario;

import br.com.tavernaonline.diario.dto.AnotacaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnotacaoMapper {

    @Mapping(target = "campanhaId", source = "campanha.id")
    @Mapping(target = "sessaoId", source = "sessao.id")
    @Mapping(target = "autorId", source = "autor.id")
    @Mapping(target = "autorNome", source = "autor.nome")
    AnotacaoResponse toResponse(Anotacao anotacao);
}
