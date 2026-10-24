package br.com.tavernaonline.diario.dto;

import br.com.tavernaonline.diario.VisibilidadeAnotacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AnotacaoRequest(
        @NotBlank @Size(max = 160) String titulo,
        @NotBlank String conteudo,
        @NotNull VisibilidadeAnotacao visibilidade,
        UUID sessaoId
) {
}
