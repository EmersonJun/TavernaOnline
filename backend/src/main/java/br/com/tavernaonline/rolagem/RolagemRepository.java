package br.com.tavernaonline.rolagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolagemRepository extends JpaRepository<Rolagem, UUID> {

    List<Rolagem> findBySessaoIdAndSecretaFalseOrderByCriadoEmAsc(UUID sessaoId);
}
