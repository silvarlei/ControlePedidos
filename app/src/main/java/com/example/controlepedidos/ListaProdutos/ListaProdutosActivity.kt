package com.example.controlepedidos.ListaProdutos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_lista_produtos.*
import android.support.v7.widget.GridLayoutManager

import android.support.v7.widget.RecyclerView
import android.widget.AdapterView
import com.example.recycleview_click_listener.ItemClickListener
import android.R.attr.name
import android.R.attr.description
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.bluetooth.BluetoothAdapter
import android.graphics.BitmapFactory
import android.support.v4.print.PrintHelper
import com.example.controlepedidos.*
import com.example.controlepedidos.Pagamento.PagamentoActivity
import com.orhanobut.hawk.Hawk
import java.io.Serializable
import java.lang.Double
import java.text.NumberFormat
import java.util.*


class ListaProdutosActivity() : AppCompatActivity() , listaProdutosContrato.View  {

    lateinit var listaProdutosAdapter: ListaProdutosAdapter


    val list = arrayListOf<produtosEntidade>()
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.controlepedidos.R.layout.activity_lista_produtos)

        if(supportActionBar != null)
        {
            supportActionBar?.hide()
        }

        val presenter = ListaProutosPresenter(this)


        presenter.listarProdutos(true)

        Comprar.setOnClickListener{


            val intent = Intent(this, PagamentoActivity::class.java)

            intent.putExtra("valorPagamento", textValor.text)
           // intent.putExtra("listaProduto",list)
            Hawk.put("produtos", list)
            startActivity(intent)
        }
            DeleteList()
            initList()

    }

    private fun DeleteList(){
        Hawk.init(this).build()

        if( Hawk.contains( "produtos" ) ){
            //Hawk.deleteAll()
            Hawk.delete( "produtos" )
        }


    }

    private fun initList(){
       // Hawk.init(this).build()

        if( !Hawk.contains( "produtos" ) ){
            Hawk.put("produtos", list)
        }

        list.addAll( Hawk.get("produtos") )
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mostraAlgo(produtosEntidade: ProdutoListaEntidade) {
       val veiculoList: MutableList<produtosEntidade> = produtosEntidade.produtos.toMutableList()

        val mGridLayoutManager = GridLayoutManager(this, 2)
        listaprodutos.setLayoutManager(mGridLayoutManager)

        listaProdutosAdapter = ListaProdutosAdapter(this, veiculoList , {produtosEntidade -> personItemClicked(produtosEntidade)} )
        listaprodutos.adapter = listaProdutosAdapter
       // listaprodutos.layoutManager = LinearLayoutManager(this)
       // listaprodutos.smoothScrollToPosition(veiculoList.size)
        listaprodutos.smoothScrollToPosition(0)



    }
    private fun personItemClicked(person: produtosEntidade) {
        //Toast.makeText(this, "Clicked: ${person.valor}", Toast.LENGTH_SHORT).show()
        var valor = textValor.text.toString().replace("R$","").replace(",",".")

        var valorSomado =  Double.parseDouble( valor) +  Double.parseDouble(person.valor.toString())

        val d = valorSomado
        val ptBr = Locale("pt", "BR")
        val valorString = NumberFormat.getCurrencyInstance(ptBr).format(d)
        textValor.setText(valorString)

        list.add(person)

    }
    override fun mostraErro(texto: String) {
        Toast.makeText(this,texto, Toast.LENGTH_LONG).show()
    }


}
