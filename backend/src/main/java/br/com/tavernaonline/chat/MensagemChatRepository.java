package br.com.tavernaonline.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MensagemChatRepository extends JpaRepository<MensagemChat, UUID> {

    List<MensagemChat> findBySessaoIdOrderByCriadoEmAsc(UUID sessaoId);

    List<MensagemChat> findBySessaoIdOrderByCriadoEmDesc(UUID sessaoId, Pageable pageable);
}
