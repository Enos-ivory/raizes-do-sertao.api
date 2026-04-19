package br.com.raizesdonordeste.domain.api.controllers;

import br.com.raizesdonordeste.domain.api.dtos.PedidoRequestDTO;
import br.com.raizesdonordeste.domain.services.PedidoService;
import br.com.raizesdonordeste.domain.entities.Pedido;
import br.com.raizesdonordeste.domain.enums.CanalPedido;
import br.com.raizesdonordeste.domain.infra.repositories.PedidoRepository;
import jakarta.validation.Valid;
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
}