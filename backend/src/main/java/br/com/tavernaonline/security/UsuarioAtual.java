package br.com.tavernaonline.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsuarioAtual {

    public UUID getId() {
        return autenticado().getId();
    }

    public String getEmail() {
        return autenticado().getUsername();
    }

    private UsuarioAutenticado autenticado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            throw new IllegalStateException("Nenhum usuario autenticado no contexto");
        }
        return usuario;
    }
}
