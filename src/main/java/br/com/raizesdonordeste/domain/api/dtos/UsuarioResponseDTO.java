package br.com.raizesdonordeste.domain.api.dtos;

import br.com.raizesdonordeste.domain.enums.Perfil;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        boolean aceiteTermosLgpd,
        Integer pontosFidelidade
) {
}