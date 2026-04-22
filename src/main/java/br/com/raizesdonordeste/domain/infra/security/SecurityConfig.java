package br.com.raizesdonordeste.domain.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilitado para APIs REST
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll() // Cadastro é público
                        .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll() // Cardápio é público

                        // EXCLUSIVO ADMIN: Apenas administradores podem alterar o status do pedido
                        .requestMatchers(HttpMethod.PATCH, "/pedidos/*/status").hasAuthority("ROLE_ADMIN")

                        // QUALQUER LOGADO: Clientes e Admins podem ver/criar pedidos
                        .requestMatchers("/pedidos/**").authenticated()

                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults()); // Permite testar com Basic Auth no Postman

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}