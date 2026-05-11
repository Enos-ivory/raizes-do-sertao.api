package br.com.raizesdonordeste.domain.api.dtos;

import br.com.raizesdonordeste.domain.enums.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        String clienteNome,
        LocalDateTime dataPedido,
        StatusPedido status,
        BigDecimal total,
        BigDecimal troco,
        List<ItemPedidoDTO> itens
) {
    public record ItemPedidoDTO(
            String produtoNome,
            Integer quantidade,
            BigDecimal precoUnitario
    ) {}
}