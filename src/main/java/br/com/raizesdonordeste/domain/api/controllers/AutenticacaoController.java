package br.com.raizesdonordeste.domain.api.controllers;

import br.com.raizesdonordeste.domain.api.dtos.LoginDTO;
import br.com.raizesdonordeste.domain.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails; // Importação nova
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        // 1. Aqui a mágica acontece: Pegamos o usuário no formato do Spring (UserDetails)
        var usuarioSpring = (UserDetails) authentication.getPrincipal();

        // 2. Extraímos o e-mail dele e mandamos para o TokenService
        String tokenJWT = tokenService.gerarToken(usuarioSpring.getUsername());

        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT, "Bearer"));
    }

    public record TokenResponseDTO(String token, String tipo) {}
}