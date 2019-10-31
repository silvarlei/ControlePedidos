package com.example.controlepedidos;

import com.example.controlepedidos.ListaProdutos.ListaProdutosService;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitConfig {

    private final Retrofit retrofit;

    public RetrofitConfig() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://www.mocky.io/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }


    public ListaProdutosService getVeiculoService() {
        return this.retrofit.create(ListaProdutosService.class);
    }
}
