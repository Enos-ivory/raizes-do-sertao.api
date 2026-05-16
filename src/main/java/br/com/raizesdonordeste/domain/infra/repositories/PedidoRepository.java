package br.com.raizesdonordeste.domain.infra.repositories;

import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.CanalPedido;
import br.com.raizesdonordeste.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCanalPedido(CanalPedido canalPedido);

    List<Pedido> findByUsuario(Usuario usuarioLogado);
}