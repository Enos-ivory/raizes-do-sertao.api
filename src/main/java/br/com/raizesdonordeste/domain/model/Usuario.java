package br.com.raizesdonordeste.domain.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // O Lombok gera getters e setters automaticamente
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha; // Aqui vai o Hash!
}