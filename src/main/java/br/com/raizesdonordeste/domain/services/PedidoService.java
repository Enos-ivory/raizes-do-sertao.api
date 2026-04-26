package br.com.raizesdonordeste.domain.services;

import br.com.raizesdonordeste.domain.api.dtos.PedidoRequestDTO;
import br.com.raizesdonordeste.domain.entities.ItemPedido;
import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.infra.repositories.PedidoRepository;
import br.com.raizesdonordeste.domain.infra.repositories.ProdutoRepository;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import br.com.raizesdonordeste.domain.model.Produto;
import br.com.raizesdonordeste.domain.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCanalPedido(dto.getCanalPedido());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<ItemPedido> itensEntidade = new ArrayList<>();

        // Validação de estoque e cálculo de preço
        for (var itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + itemDto.getProdutoId()));

            if (produto.getEstoque() < itemDto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente: " + produto.getNome());
            }

            // Atualiza estoque
            produto.setEstoque(produto.getEstoque() - itemDto.getQuantidade());
            produtoRepository.save(produto);

            // Cria o item
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setPedido(pedido);
            itensEntidade.add(item);

            valorTotal = valorTotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade())));
        }

        // Desconto de 10% acima de R$ 100
        if (valorTotal.compareTo(new BigDecimal("100.00")) > 0) {
            valorTotal = valorTotal.multiply(new BigDecimal("0.90"));
        }

        // VALIDAÇÃO FINANCEIRA: Verifica se o valor pago cobre o total

        if (dto.getValorPagamento().compareTo(valorTotal) < 0) {
            throw new RuntimeException("Erro: Valor entregue (R$ " + dto.getValorPagamento() +
                    ") é menor que o total do pedido (R$ " + valorTotal + ")");
        }

        // CALCULA O TROCO
        BigDecimal troco = dto.getValorPagamento().subtract(valorTotal);

        // Preenche os dados financeiros no pedido
        pedido.setItens(itensEntidade);
        pedido.setTotal(valorTotal);
        pedido.setValorEntregue(dto.getValorPagamento());
        pedido.setTroco(troco);

        // Salva o pedido inicial
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Processa pagamento simulado
        boolean pagamentoAprovado = pagamentoMockService.processarPagamento(
                pedidoSalvo.getTotal(),
                pedidoSalvo.getFormaPagamento()
        );

        if (pagamentoAprovado) {
            pedidoSalvo.setStatus(StatusPedido.PAGO);
            processarFidelidade(usuario, pedidoSalvo.getTotal());
        } else {
            pedidoSalvo.setStatus(StatusPedido.CANCELADO);
        }

        return pedidoRepository.save(pedidoSalvo);
    }

    private void processarFidelidade(Usuario usuario, BigDecimal valorPedido) {
        if (usuario.isAceiteTermosLgpd()) {
            int novosPontos = valorPedido.intValue();
            int pontosAtuais = usuario.getPontosFidelidade() != null ? usuario.getPontosFidelidade() : 0;
            usuario.setPontosFidelidade(pontosAtuais + novosPontos);
            usuarioRepository.save(usuario);
        }
    }
}