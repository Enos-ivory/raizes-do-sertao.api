package br.com.raizesdonordeste.domain.api.controllers;


import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

   @Autowired
   private UsuarioService service;

   @PostMapping
   private void cadastrar(@RequestBody Usuario usuario){
       service.salvarComSenhaSegura(usuario);
   }
   @GetMapping
    public List<Usuario> listar(){
       return service.listarTodos();
   }

    // === NOVO MÉTODO PARA BUSCAR POR ID ===
    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}