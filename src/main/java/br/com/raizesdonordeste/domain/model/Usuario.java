package br.com.raizesdonordeste.domain.model;

import br.com.raizesdonordeste.domain.enums.Perfil;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
public class Usuario implements UserDetails { // Implementação obrigatória
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    private boolean consentimentoTermos;
    private LocalDateTime dataConsentimento;
    private boolean aceiteTermosLgpd;

    private Integer pontosFidelidade = 0;
    private String finalidadeDados = "Execução de contrato e autenticação";

    @PrePersist
    protected void onCreate() {
        if (aceiteTermosLgpd) {
            this.dataConsentimento = LocalDateTime.now();
        }
    }

    // MÉTODOS DO USERDETAILS (Resolve o erro do SecurityFilter)


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String roleNome = perfil.name().startsWith("ROLE_") ? perfil.name() : "ROLE_" + perfil.name();
        return List.of(new SimpleGrantedAuthority(roleNome));
    }
    @Override
    public String getPassword() {
        return senha;
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

    public void setConsentimentoLgpd(boolean b) {
    }
}