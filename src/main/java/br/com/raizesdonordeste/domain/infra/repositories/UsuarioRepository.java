package br.com.raizesdonordeste.domain.infra.repositories;

import br.com.raizesdonordeste.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O JpaRepository já te dá save(), findAll(), deleteById(), etc.
}