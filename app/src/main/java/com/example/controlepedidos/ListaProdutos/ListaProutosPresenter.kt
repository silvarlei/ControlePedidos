package com.example.controlepedidos.ListaProdutos
import com.example.controlepedidos.ProdutoListaEntidade
import com.example.controlepedidos.RetrofitConfig
import com.example.controlepedidos.produtosEntidade
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaProutosPresenter(private val view: listaProdutosContrato.View) : listaProdutosContrato.Presenter {

    val database = FirebaseDatabase.getInstance()
    var ref = database.getReference("server/saving-data/fireblog/posts")


    override fun listarProdutosFirebase(status: Boolean) {
        try
        {
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Post newPost = dataSnapshot.getValue(Post.class);
                System.out.println("Author: " + newPost.author);
                System.out.println("Title: " + newPost.title);
                System.out.println("Previous Post ID: " + prevChildKey);
            }
        }
        catch (e: Exception)
        {
            view.mostraErro("erroo")
        }

    }

    override fun listarProdutos(status: Boolean) {
        try
        {

            val call = RetrofitConfig().getVeiculoService().veiculoHistorico("5d278862320000c52971bbbe")
            call.enqueue(object: Callback<ProdutoListaEntidade> {
                override fun onResponse(call: Call<ProdutoListaEntidade>?,
                                        response: Response<ProdutoListaEntidade>?) {
                    val cep = response?.body()

                    if (cep != null) {
                        view.mostraAlgo(cep)
                    }
                    else
                    {
                        view.mostraErro("Erro")
                    }

                }

                override fun onFailure(call: Call<ProdutoListaEntidade?>?, t: Throwable?){
                    view.mostraErro(t.toString())
                }
            })
        }
        catch (e: Exception)
        {
            view.mostraErro("erroo")
        }

    }


}