package br.com.tavernaonline.sessao.presenca;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresencaService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<UUID, Map<String, String>> salas = new ConcurrentHashMap<>();
    private final Map<String, UUID> sessaoPorConexao = new ConcurrentHashMap<>();

    public PresencaService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void entrar(UUID sessaoId, String conexaoId, String nome) {
        salas.computeIfAbsent(sessaoId, id -> new ConcurrentHashMap<>()).put(conexaoId, nome);
        sessaoPorConexao.put(conexaoId, sessaoId);
        publicar(sessaoId);
    }

    public void sair(String conexaoId) {
        UUID sessaoId = sessaoPorConexao.remove(conexaoId);
        if (sessaoId == null) {
            return;
        }
        Map<String, String> sala = salas.get(sessaoId);
        if (sala != null) {
            sala.remove(conexaoId);
            if (sala.isEmpty()) {
                salas.remove(sessaoId);
            }
        }
        publicar(sessaoId);
    }

    public List<String> online(UUID sessaoId) {
        Map<String, String> sala = salas.get(sessaoId);
        if (sala == null) {
            return List.of();
        }
        return sala.values().stream().distinct().sorted().toList();
    }

    private void publicar(UUID sessaoId) {
        messagingTemplate.convertAndSend(
                "/topic/sessao." + sessaoId + ".presenca", new ArrayList<>(online(sessaoId)));
    }
}
