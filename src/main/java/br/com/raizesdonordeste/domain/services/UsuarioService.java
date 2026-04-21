package br.com.raizesdonordeste.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public List<Usuario> listarTodos() {
        // Registro de log para conformidade LGPD
        logger.info("ACESSO SENSÍVEL: Lista de utilizadores consultada para fins de auditoria.");
        return repository.findAll();
    }

    @Autowired
    private UsuarioRepository repository;

    public void salvarComSenhaSegura(Usuario usuario) {
        // Aplica o hash na senha antes de salvar (Segurança)
        String hash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(hash);
        repository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public void anonimizarUsuario(Long id) {
        repository.findById(id).ifPresent(usuario -> {
            usuario.setNome("USUÁRIO ANONIMIZADO");
            usuario.setEmail("anonimo@raizesdonordeste.com");
            usuario.setSenha("********"); // Remove o hash original
            repository.save(usuario);
            logger.warn("LGPD: Dados do utilizador ID {} foram anonimizados.", id);
        });
    }


}
