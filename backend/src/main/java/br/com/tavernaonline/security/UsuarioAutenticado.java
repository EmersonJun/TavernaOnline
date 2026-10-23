package br.com.tavernaonline.security;

import br.com.tavernaonline.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioAutenticado implements UserDetails {

    private final UUID id;
    private final String email;
    private final String senhaHash;

    public UsuarioAutenticado(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.senhaHash = usuario.getSenhaHash();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
