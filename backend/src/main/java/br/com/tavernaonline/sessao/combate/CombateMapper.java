package br.com.tavernaonline.sessao.combate;

import br.com.tavernaonline.sessao.combate.dto.CombatenteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CombateMapper {

    @Mapping(target = "personagemId", source = "personagem.id")
    CombatenteResponse toResponse(ParticipanteCombate participante);
}
