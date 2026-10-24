package br.com.tavernaonline.sessao;

import br.com.tavernaonline.sessao.dto.SessaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessaoMapper {

    @Mapping(target = "campanhaId", source = "campanha.id")
    SessaoResponse toResponse(Sessao sessao);
}
