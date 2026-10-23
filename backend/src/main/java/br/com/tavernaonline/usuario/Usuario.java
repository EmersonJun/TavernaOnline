package br.com.tavernaonline.usuario;

import br.com.tavernaonline.common.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends EntidadeBase {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    @PrePersist
    private void aoPersistir() {
        garantirId();
    }
}
