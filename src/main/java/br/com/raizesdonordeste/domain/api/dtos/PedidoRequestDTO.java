package br.com.raizesdonordeste.domain.api.dtos;

import br.com.raizesdonordeste.domain.enums.CanalPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PedidoRequestDTO {

    @NotNull(message = "O canalPedido é obrigatório (ex: TOTEM, APP)")
    private CanalPedido canalPedido;

    @NotNull(message = "clienteId é obrigatório")
    private Long clienteId;

    @NotNull
    @Positive(message = "O total deve ser maior que zero")
    private BigDecimal total;

    @NotNull(message = "A formaPagamento é obrigatória")
    private String formaPagamento;

    public CanalPedido getCanalPedido() { return canalPedido; }
    public void setCanalPedido(CanalPedido canalPedido) { this.canalPedido = canalPedido; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
}