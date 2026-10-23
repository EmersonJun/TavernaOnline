package br.com.tavernaonline.campanha;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipacaoRepository extends JpaRepository<Participacao, UUID> {

    boolean existsByUsuarioIdAndCampanhaId(UUID usuarioId, UUID campanhaId);

    boolean existsByUsuarioIdAndCampanhaIdAndPapel(UUID usuarioId, UUID campanhaId, Papel papel);

    Optional<Participacao> findByUsuarioIdAndCampanhaId(UUID usuarioId, UUID campanhaId);

    List<Participacao> findByCampanhaId(UUID campanhaId);

    List<Participacao> findByUsuarioId(UUID usuarioId);
}
