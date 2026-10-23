package br.com.tavernaonline.campanha;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampanhaRepository extends JpaRepository<Campanha, UUID> {

    Optional<Campanha> findByCodigoConvite(String codigoConvite);

    boolean existsByCodigoConvite(String codigoConvite);
}
