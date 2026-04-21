package br.com.raizesdonordeste.domain.model;


import br.com.raizesdonordeste.domain.enums.Perfil;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data // O Lombok gera getters e setters automaticamente
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha; // Aqui vai o Hash!

    @Enumerated(EnumType.STRING)
    private Perfil perfil;
    // Requisito LGPD: Demonstração de consentimento
    private boolean consentimentoTermos; // true se aceitou
    private LocalDateTime dataConsentimento;
    private boolean aceiteTermosLgpd;

    // Campo para armazenar os pontos acumulados
    private Integer pontosFidelidade = 0;

    // Requisito LGPD: Finalidade e Base Legal (Armazenado como metadado ou log)
    private String finalidadeDados = "Execução de contrato e autenticação";
}