package br.com.raizesdonordeste.domain.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @GetMapping
    public String login(Principal principal) {
        // Se chegar aqui, o Spring Security já validou o utilizador
        return "Utilizador " + principal.getName() + " autenticado com sucesso!";
    }
}