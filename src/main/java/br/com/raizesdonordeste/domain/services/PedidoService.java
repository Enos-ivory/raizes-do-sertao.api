package br.com.raizesdonordeste.domain.services;

import br.com.raizesdonordeste.domain.api.dtos.PedidoRequestDTO;
import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.infra.repositories.PedidoRepository;
import br.com.raizesdonordeste.domain.infra.repositories.ProdutoRepository;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import br.com.raizesdonordeste.domain.model.Produto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagamentoMockService pagamentoMockService;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         PagamentoMockService pagamentoMockService,
                         UsuarioRepository usuarioRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pagamentoMockService = pagamentoMockService;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Pedido realizarPedido(@Valid PedidoRequestDTO dto) {
        // 1. VALIDAÇÃO DE ESTOQUE
        if (dto.getItens() != null) {
            for (var itemDto : dto.getItens()) {
                Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + itemDto.getProdutoId()));

                if (produto.getEstoque() < itemDto.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                }

                // Baixa o estoque
                produto.setEstoque(produto.getEstoque() - itemDto.getQuantidade());
                produtoRepository.save(produto);
            }
        }

        Pedido pedido = new Pedido();
        pedido.setCanalPedido(dto.getCanalPedido());
        pedido.setClienteId(dto.getClienteId());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // 2. LÓGICA DE PROMOÇÃO (10% desconto acima de 100 reais)
        BigDecimal valorFinal = dto.getTotal();
        if (valorFinal.compareTo(new BigDecimal("100.00")) > 0) {
            valorFinal = valorFinal.multiply(new BigDecimal("0.90"));
        }
        pedido.setTotal(valorFinal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        boolean pagamentoAprovado = pagamentoMockService.processarPagamento(
                pedidoSalvo.getTotal(),
                pedidoSalvo.getFormaPagamento()
        );

        if (pagamentoAprovado) {
            pedidoSalvo.setStatus(StatusPedido.PAGO);
            // 3. LÓGICA DE FIDELIDADE (LGPD)
            processarFidelidade(pedidoSalvo.getClienteId(), pedidoSalvo.getTotal());
        } else {
            pedidoSalvo.setStatus(StatusPedido.CANCELADO);
        }

        return pedidoRepository.save(pedidoSalvo);
    }

    private void processarFidelidade(Long clienteId, BigDecimal valorPedido) {
        usuarioRepository.findById(clienteId).ifPresent(usuario -> {
            if (usuario.isAceiteTermosLgpd()) {
                int novosPontos = valorPedido.intValue();
                int pontosAtuais = usuario.getPontosFidelidade() != null ? usuario.getPontosFidelidade() : 0;
                usuario.setPontosFidelidade(pontosAtuais + novosPontos);
                usuarioRepository.save(usuario);
            }
        });
    }
}