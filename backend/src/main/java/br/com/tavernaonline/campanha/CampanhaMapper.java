package br.com.tavernaonline.campanha;

import br.com.tavernaonline.campanha.dto.CampanhaResponse;
import br.com.tavernaonline.campanha.dto.ParticipanteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampanhaMapper {

    @Mapping(target = "meuPapel", source = "papel")
    CampanhaResponse toResponse(Campanha campanha, Papel papel);

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "nome", source = "usuario.nome")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "papel", source = "papel")
    ParticipanteResponse toParticipante(Participacao participacao);
}
