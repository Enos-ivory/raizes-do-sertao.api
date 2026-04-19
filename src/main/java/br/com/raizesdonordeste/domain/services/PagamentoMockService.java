package br.com.raizesdonordeste.domain.services;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PagamentoMockService {

    public boolean processarPagamento(BigDecimal valor, String formaPagamento) {
        // Simulação de gateway de pagamento para o projeto
        if (valor.compareTo(new BigDecimal("8000")) > 0) {
            return false;
        }
        return true;
    }
}