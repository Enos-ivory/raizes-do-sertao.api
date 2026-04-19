package br.com.raizesdonordeste.domain.infra.repositories;

import br.com.raizesdonordeste.domain.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Ao estender JpaRepository, ganhas automaticamente métodos como:
    // save(), findAll(), findById(), deleteById(), etc.
}