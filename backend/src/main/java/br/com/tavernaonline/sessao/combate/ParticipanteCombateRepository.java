package br.com.tavernaonline.sessao.combate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipanteCombateRepository extends JpaRepository<ParticipanteCombate, UUID> {

    List<ParticipanteCombate> findBySessaoIdOrderByOrdemAsc(UUID sessaoId);

    void deleteBySessaoId(UUID sessaoId);
}
