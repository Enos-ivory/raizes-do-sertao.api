package br.com.raizesdonordeste.domain.api.controllers;
import br.com.raizesdonordeste.domain.enums.StatusPedido;
import br.com.raizesdonordeste.domain.api.dtos.PedidoRequestDTO;
import br.com.raizesdonordeste.domain.infra.repositories.ProdutoRepository;
import br.com.raizesdonordeste.domain.model.Produto;
import br.com.raizesdonordeste.domain.services.PedidoService;
import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.CanalPedido;
import br.com.raizesdonordeste.domain.infra.repositories.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@Valid @RequestBody PedidoRequestDTO request) {
        Pedido novoPedido = pedidoService.realizarPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos(@RequestParam(required = false) CanalPedido canalPedido) {
        if (canalPedido != null) {
            return ResponseEntity.ok(pedidoRepository.findByCanalPedido(canalPedido));
        }
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(@PathVariable Long id, @RequestBody StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }
    @Autowired
    private ProdutoRepository repository; // ADICIONA ESTA LINHA PARA ACABAR COM O ERRO

    @GetMapping("/unidade/{unidade}")
    public ResponseEntity<List<Produto>> listarPorUnidade(@PathVariable String unidade) {
        List<Produto> cardapio = repository.findByUnidade(unidade);
        return ResponseEntity.ok(cardapio);
    }
}