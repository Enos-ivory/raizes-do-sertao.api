package br.com.raizesdonordeste.domain.services;

import br.com.raizesdonordeste.domain.model.Produto;
import br.com.raizesdonordeste.domain.model.Usuario;
import br.com.raizesdonordeste.domain.enums.Perfil;
import br.com.raizesdonordeste.domain.infra.repositories.ProdutoRepository;
import br.com.raizesdonordeste.domain.infra.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {


        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador Raízes");
            admin.setEmail("admin@raizesdonordeste.com");
            // Criptografa a senha padrão usando o BCrypt do seu projeto
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setPerfil(Perfil.ROLE_ADMIN);
            admin.setConsentimentoLgpd(true);

            usuarioRepository.save(admin);
            System.out.println("🌱 SEED: Usuário Administrador padrão criado com sucesso! (admin@raizesdonordeste.com / admin123)");
        }

        // SEED DE PRODUTOS BÁSICOS SE O CARDAPIO ESTIVER VAZIO
        if (produtoRepository.count() == 0) {

            Produto p1 = new Produto();
            p1.setNome("Baião de Dois");
            p1.setPreco(new BigDecimal("35.00"));
            p1.setQuantidadeEstoque(50);
            produtoRepository.save(p1);

            Produto p2 = new Produto();
            p2.setNome("Carne de Sol com Macaxeira");
            p2.setPreco(new BigDecimal("42.50"));
            p2.setQuantidadeEstoque(30);
            produtoRepository.save(p2);

            Produto p3 = new Produto();
            p3.setNome("Suco de Caju Nativo");
            p3.setPreco(new BigDecimal("8.00"));
            p3.setQuantidadeEstoque(100);
            produtoRepository.save(p3);

            System.out.println("🌱 SEED: Cardápio inicial populado com produtos bases!");
        }
    }
}