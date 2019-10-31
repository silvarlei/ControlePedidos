package com.example.controlepedidos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProdutoListaEntidade {
    public produtosEntidade[] produtos;

    @JsonProperty("produtos")
    public produtosEntidade[] getProdutos() { return produtos; }
    @JsonProperty("produtos")
    public void setProdutos(produtosEntidade[] value) { this.produtos = value; }
}
