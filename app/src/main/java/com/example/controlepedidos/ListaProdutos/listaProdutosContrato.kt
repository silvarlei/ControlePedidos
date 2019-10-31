package com.example.controlepedidos.ListaProdutos

import com.example.controlepedidos.ProdutoListaEntidade
import com.example.controlepedidos.produtosEntidade


interface listaProdutosContrato{
    interface  View
    {
        fun showLoading()
        fun hideLoading()
        fun mostraAlgo(produtosEntidade: ProdutoListaEntidade)
        fun mostraErro(texto: String)

    }

    interface Presenter{
        fun listarProdutos(status: Boolean  )

    }
}