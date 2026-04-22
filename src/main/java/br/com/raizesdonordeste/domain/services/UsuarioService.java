package br.com.raizesdonordeste.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.enums.Perfil;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Use o oficial do Spring
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o Bean que está no SecurityConfig

    public List<Usuario> listarTodos() {
        logger.info("ACESSO SENSÍVEL: Lista de utilizadores consultada para fins de auditoria (LGPD).");
        return repository.findAll();
    }

    public void salvarComSenhaSegura(Usuario usuario) {
        // 1. Criptografia compatível com AutenticacaoService
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        // 2. Lógica de Perfis (Roles): Define um padrão caso não seja enviado
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(Perfil.ROLE_CLIENTE);
        }

        repository.save(usuario);
        logger.info("SEGURANÇA: Novo usuário cadastrado com perfil {}.", usuario.getPerfil());
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public void anonimizarUsuario(Long id) {
        repository.findById(id).ifPresent(usuario -> {
            usuario.setNome("USUÁRIO ANONIMIZADO");
            usuario.setEmail("anonimo@raizesdonordeste.com");
            usuario.setSenha("********");
            repository.save(usuario);
            logger.warn("LGPD: Dados do utilizador ID {} foram anonimizados por solicitação do titular.", id);
        });
    }
}