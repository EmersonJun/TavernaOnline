package br.com.tavernaonline.personagem;

import br.com.tavernaonline.personagem.dto.AtributosDto;
import br.com.tavernaonline.personagem.dto.ItemInventarioDto;
import br.com.tavernaonline.personagem.dto.PericiaDto;
import br.com.tavernaonline.personagem.dto.PersonagemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonagemMapper {

    @Mapping(target = "campanhaId", source = "campanha.id")
    @Mapping(target = "donoId", source = "dono.id")
    PersonagemResponse toResponse(Personagem personagem);

    Atributos toAtributos(AtributosDto dto);

    List<Pericia> toPericias(List<PericiaDto> pericias);

    List<ItemInventario> toInventario(List<ItemInventarioDto> inventario);
}
