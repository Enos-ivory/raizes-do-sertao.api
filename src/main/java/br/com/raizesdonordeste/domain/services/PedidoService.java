package br.com.raizesdonordeste.domain.services;

import br.com.raizesdonordeste.domain.api.dtos.PedidoRequestDTO;
import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.infra.repositories.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagamentoMockService pagamentoMockService;

    public PedidoService(PedidoRepository pedidoRepository, PagamentoMockService pagamentoMockService) {
        this.pedidoRepository = pedidoRepository;
        this.pagamentoMockService = pagamentoMockService;
    }

    @Transactional
    public Pedido realizarPedido(@Valid PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setCanalPedido(dto.getCanalPedido());
        pedido.setClienteId(dto.getClienteId());
        pedido.setTotal(dto.getTotal());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        boolean pagamentoAprovado = pagamentoMockService.processarPagamento(
                pedidoSalvo.getTotal(),
                pedidoSalvo.getFormaPagamento()
        );

        if (pagamentoAprovado) {
            pedidoSalvo.setStatus(StatusPedido.PAGO);
        } else {
            pedidoSalvo.setStatus(StatusPedido.CANCELADO);
        }

        return pedidoRepository.save(pedidoSalvo);
    }
}