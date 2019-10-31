package com.example.controlepedidos.ListaProdutos

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.controlepedidos.produtosEntidade
import kotlinx.android.synthetic.main.itemprodutos.view.*
import com.squareup.picasso.Picasso






class ListaProdutosAdapter(private val context: Context, private var pessoaList: MutableList<produtosEntidade> ,  private val clickListener: (produtosEntidade) -> Unit):
    RecyclerView.Adapter<ListaProdutosAdapter.VeiculoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VeiculoViewHolder {
        val view = LayoutInflater.from(context).inflate(com.example.controlepedidos.R.layout.itemprodutos, parent, false)
        return VeiculoViewHolder(view)
    }

    override fun getItemCount() = pessoaList.size



    override fun onBindViewHolder(holder: VeiculoViewHolder, position: Int) {
        holder.bindView(pessoaList[position] , clickListener)


    }



    class VeiculoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewNome = itemView.textViewNome
        val textViewEmail = itemView.textViewEmail
        val textViewTelefone = itemView.textViewTelefone
        val image_view  = itemView.image_view

        fun bindView(dado: produtosEntidade , clickListener: (produtosEntidade) -> Unit) {

            textViewNome.setText(  dado?.descricao?.toString())
            textViewEmail.setText( dado?.valor?.toString())
            textViewTelefone.setText( dado?.descricao?.toString())

            Picasso.with(itemView.context).load(dado?.imagem?.toString())
                .placeholder(com.example.controlepedidos.R.mipmap.ic_launcher_round)// optional
                //.error(R.drawable.sync)// optional
                .into(image_view);

            itemView.setOnClickListener { clickListener(dado)}
        }
    }

}

