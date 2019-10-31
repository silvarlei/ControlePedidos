package com.example.controlepedidos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class produtosEntidade {
     private String imagem;
     private String valor;
     private String descricao;

     @JsonProperty("imagem")
     public String getImagem() { return imagem; }
     @JsonProperty("imagem")
     public void setImagem(String value) { this.imagem = value; }

     @JsonProperty("valor")
     public String getValor() { return valor; }
     @JsonProperty("valor")
     public void setValor(String value) { this.valor = value; }

     @JsonProperty("descricao")
     public String getDescricao() { return descricao; }
     @JsonProperty("descricao")
     public void setDescricao(String value) { this.descricao = value; }
}
