package br.com.raizesdonordeste.domain.api.controllers;

import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.api.dtos.PedidoRequestDTO;
import br.com.raizesdonordeste.domain.api.dtos.PedidoResponseDTO;
import br.com.raizesdonordeste.domain.infra.repositories.ProdutoRepository;
import br.com.raizesdonordeste.domain.model.Produto;
import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.services.PedidoService;
import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.CanalPedido;
import br.com.raizesdonordeste.domain.infra.repositories.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository; // Ajustado para injeção limpa

    public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarMeusPedidos(
            @AuthenticationPrincipal Usuario usuarioLogado // <-- Pegamos quem está logado pelo Token!
    ) {
        // Busca no banco apenas os pedidos desse usuário
        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuarioLogado);

        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @Valid @RequestBody PedidoRequestDTO request,
            @AuthenticationPrincipal Usuario usuarioLogado // <-- O Spring preenche com o dono do Token
    ) {
        // Agora passamos o request e o usuário logado para o Service
        Pedido novoPedido = pedidoService.realizarPedido(request, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(converterParaDTO(novoPedido));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos(@RequestParam(required = false) CanalPedido canalPedido) {
        List<Pedido> pedidos;
        if (canalPedido != null) {
            pedidos = pedidoRepository.findByCanalPedido(canalPedido);
        } else {
            pedidos = pedidoRepository.findAll();
        }


        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestBody StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return ResponseEntity.ok(converterParaDTO(pedidoSalvo));
    }

    /// Exibicao cardapio
    @GetMapping("/unidade/{unidade}")
    public ResponseEntity<List<Produto>> listarPorUnidade(@PathVariable String unidade) {
        List<Produto> cardapio = produtoRepository.findByUnidade(unidade);
        return ResponseEntity.ok(cardapio);
    }

    private PedidoResponseDTO converterParaDTO(Pedido pedido) {
        String nomeCliente = (pedido.getUsuario() != null) ? pedido.getUsuario().getNome(): "Cliente nao identificado";
        return new PedidoResponseDTO(
                pedido.getId(),
                nomeCliente,
              ///  pedido.getUsuario().getNome(), // Exibe apenas o nome, oculta senha/email/lgpd
                pedido.getDataPedido(),
                pedido.getStatus(),
                pedido.getTotal(),
                pedido.getTroco(),
                pedido.getItens().stream()
                        .map(item -> new PedidoResponseDTO.ItemPedidoDTO(
                                item.getProduto().getNome(),
                                item.getQuantidade(),
                                item.getPrecoUnitario()
                        )).toList()
        );
    }
}