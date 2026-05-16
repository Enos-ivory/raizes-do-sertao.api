package br.com.raizesdonordeste.domain.api.controllers;

import br.com.raizesdonordeste.domain.api.dtos.UsuarioResponseDTO;
import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    // Injeção via Construtor (Recomendado)
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody Usuario usuario) {
        service.salvarComSenhaSegura(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return listarTodos(); // Reutiliza a lógica de listagem segura
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        Usuario u = service.buscarPorId(id);
        UsuarioResponseDTO dto = new UsuarioResponseDTO(
                u.getId(), u.getNome(), u.getEmail(), u.getPerfil(),
                u.isAceiteTermosLgpd(), u.getPontosFidelidade()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/esquecimento")
    public ResponseEntity<Void> excluirDadosLgpd(@PathVariable Long id) {
        service.anonimizarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list-users")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        //  Usando o 'service' em vez do 'repository' que não estava declarado
        List<Usuario> usuarios = service.listarTodos();

        List<UsuarioResponseDTO> dtos = usuarios.stream()
                .map(u -> new UsuarioResponseDTO(
                        u.getId(),
                        u.getNome(),
                        u.getEmail(),
                        u.getPerfil(),
                        u.isAceiteTermosLgpd(),
                        u.getPontosFidelidade()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }
}