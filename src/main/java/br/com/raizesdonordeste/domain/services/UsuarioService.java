package br.com.raizesdonordeste.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.enums.Perfil;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; //  oficial do Spring
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
        // Criptografia compatível com AutenticacaoService
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        //  Lógica de Perfis (Roles): Define um padrão caso não seja enviado
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(Perfil.ROLE_CLIENTE);
        }

        // Ajuste LGPD: Carimba a data e hora do consentimento
        if (usuario.isAceiteTermosLgpd()) {
            usuario.setDataConsentimento(java.time.LocalDateTime.now());
        }

        repository.save(usuario);
        logger.info("SEGURANÇA: Novo usuário cadastrado com perfil {}.", usuario.getPerfil());
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public void anonimizarUsuario(Long id) {
        // Força o Java a lançar um erro caso o ID não exista no banco
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para anonimização com o ID: " + id));

        // Remove a Identificação Pessoal (PII)
        usuario.setNome("USUÁRIO ANONIMIZADO");

        // Torna o email único para não quebrar o banco, mas sem identificar o dono
        usuario.setEmail("anonimo-" + id + "@raizesdonordeste.com");

        // Remove credenciais de acesso
        usuario.setSenha(null);

        //  Revoga o consentimento (LGPD)
        usuario.setAceiteTermosLgpd(false);
        usuario.setConsentimentoTermos(false);

        repository.save(usuario);

        // Log de Auditoria para prova de conformidade
        logger.warn("LGPD: O direito ao esquecimento foi exercido para o ID {}. Dados anonimizados.", id);
    }
}