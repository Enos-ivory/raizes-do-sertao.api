package br.com.raizesdonordeste.domain.api.controllers;

import br.com.raizesdonordeste.domain.model.Produto;
import br.com.raizesdonordeste.domain.infra.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;

    // Endpoint para Cadastrar Produto
    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody Produto produto) {
        Produto novoProduto = repository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    // Endpoint para Listar todos os Produtos
    @GetMapping
    public List<Produto> listar() {
        return repository.findAll();
    }
}