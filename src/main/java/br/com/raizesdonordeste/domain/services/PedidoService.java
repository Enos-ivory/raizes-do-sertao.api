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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
    public Pedido realizarPedido(@Valid PedidoRequestDTO dto, Usuario usuarioLogado) {

        // 1. O pedido agora é estritamente vinculado ao dono do token de segurança.
        //  "clienteId" que o usuário tente mandar de forma maliciosa.
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuarioLogado);
        pedido.setCanalPedido(dto.getCanalPedido());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        BigDecimal valorTotalSemDesconto = BigDecimal.ZERO;
        List<ItemPedido> itensEntidade = new ArrayList<>();

        for (var itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + itemDto.getProdutoId()));

            // Controle de Estoque: Restrição por indisponibilidade
            if (produto.getEstoque() < itemDto.getQuantidade()) {
                log.warn("AUDITORIA: Tentativa de compra bloqueada. Estoque insuficiente para o produto ID: {}", produto.getId());
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Controle de Estoque: Saída
            produto.setEstoque(produto.getEstoque() - itemDto.getQuantidade());
            produtoRepository.save(produto);

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setPedido(pedido);
            itensEntidade.add(item);

            valorTotalSemDesconto = valorTotalSemDesconto.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade())));
        }

        BigDecimal valorFinal = valorTotalSemDesconto;
        if (valorTotalSemDesconto.compareTo(new BigDecimal("100.00")) > 0) {
            valorFinal = valorTotalSemDesconto.multiply(new BigDecimal("0.90"));
        }

        if (dto.getValorPagamento().compareTo(valorFinal) < 0) {
            throw new RuntimeException("Valor entregue insuficiente.");
        }

        pedido.setItens(itensEntidade);
        pedido.setTotal(valorFinal);
        pedido.setValorEntregue(dto.getValorPagamento());
        pedido.setTroco(dto.getValorPagamento().subtract(valorFinal));

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        boolean aprovado = pagamentoMockService.processarPagamento(pedidoSalvo.getTotal(), pedidoSalvo.getFormaPagamento());

        if (aprovado) {
            pedidoSalvo.setStatus(StatusPedido.PAGO);
            // 2. Pontos de fidelidade vão para o dono do Token
            processarFidelidade(usuarioLogado, pedidoSalvo.getTotal());

            log.info("AUDITORIA: Pedido criado e pago com sucesso. Pedido ID: {}, Cliente ID: {}, Valor Total: R$ {}",
                    pedidoSalvo.getId(), usuarioLogado.getId(), pedidoSalvo.getTotal());
        } else {
            log.error("AUDITORIA: Pagamento recusado. Cancelando Pedido ID: {} e estornando estoque.", pedidoSalvo.getId());
            cancelarPedido(pedidoSalvo.getId()); // Chama o cancelamento com estorno
            throw new RuntimeException("Pagamento negado. O estoque foi devolvido.");
        }

        return pedidoRepository.save(pedidoSalvo);
    }

    //  Atualização de status com regra de negócio
    @Transactional
    public Pedido atualizarStatusParaCozinha(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();
        if (pedido.getStatus() != StatusPedido.PAGO) {
            throw new RuntimeException("O pedido precisa estar PAGO para ir para a cozinha.");
        }
        pedido.setStatus(StatusPedido.PREPARANDO);
        return pedidoRepository.save(pedido);
    }

    //  Controle de estoque (Entrada/Estorno)
    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();

        if (pedido.getStatus() == StatusPedido.CANCELADO) return;

        // Devolve os produtos ao estoque (Controle de entrada por cancelamento)
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
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