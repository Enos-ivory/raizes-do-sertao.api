package br.com.raizesdonordeste.domain.services;

import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public void salvarComSenhaSegura(Usuario usuario) {
        // Aplica o hash na senha antes de salvar (Segurança)
        String hash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(hash);
        repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }
}