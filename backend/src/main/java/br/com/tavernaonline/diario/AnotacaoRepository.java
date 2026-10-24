package br.com.tavernaonline.diario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnotacaoRepository extends JpaRepository<Anotacao, UUID> {

    List<Anotacao> findByCampanhaIdOrderByCriadoEmDesc(UUID campanhaId);

    List<Anotacao> findByCampanhaIdAndVisibilidadeOrderByCriadoEmDesc(
            UUID campanhaId, VisibilidadeAnotacao visibilidade);
}
