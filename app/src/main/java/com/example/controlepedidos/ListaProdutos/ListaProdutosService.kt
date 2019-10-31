package com.example.controlepedidos.ListaProdutos

import com.example.controlepedidos.ProdutoListaEntidade
import com.example.controlepedidos.produtosEntidade
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ListaProdutosService {
    @GET("/v2/{id}")
    fun veiculoHistorico(@Path("id") id:String): Call<ProdutoListaEntidade>
}