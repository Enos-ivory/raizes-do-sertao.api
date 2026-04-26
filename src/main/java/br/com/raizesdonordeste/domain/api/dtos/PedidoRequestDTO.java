package br.com.raizesdonordeste.domain.api.dtos;

import br.com.raizesdonordeste.domain.enums.CanalPedido;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "O canalPedido é obrigatório (ex: TOTEM, APP)")
    private CanalPedido canalPedido;

    @NotNull(message = "clienteId é obrigatório")
    private Long clienteId;

    @NotNull(message = "A formaPagamento é obrigatória")
    private String formaPagamento;

    // Removido @NotNull e @Positive: o Service calculará o total sozinho
    private BigDecimal total;

    // Campo para os itens do pedido
    @NotNull(message = "A lista de itens não pode ser nula")
    private List<ItemPedidoRequestDTO> itens;

    // Getters e Setters
    public List<ItemPedidoRequestDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoRequestDTO> itens) { this.itens = itens; }

    public CanalPedido getCanalPedido() { return canalPedido; }
    public void setCanalPedido(CanalPedido canalPedido) { this.canalPedido = canalPedido; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public static class ItemPedidoRequestDTO {
        @NotNull(message = "ID do produto é obrigatório")
        private Long produtoId;

        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidade;

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }
}