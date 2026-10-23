package br.com.tavernaonline.personagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonagemRepository extends JpaRepository<Personagem, UUID> {

    List<Personagem> findByCampanhaId(UUID campanhaId);

    List<Personagem> findByCampanhaIdAndDonoId(UUID campanhaId, UUID donoId);
}
