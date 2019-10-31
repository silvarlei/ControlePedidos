package com.example.controlepedidos.Pagamento

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.print.PrintHelper
import android.support.v7.app.AlertDialog
import com.example.controlepedidos.Ficha.FichaActivity
import com.example.controlepedidos.produtosEntidade
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_lista_produtos.*
import kotlinx.android.synthetic.main.activity_pagamento.*
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import com.example.controlepedidos.Ficha.PrintDialogActivity
import com.example.controlepedidos.R


class PagamentoActivity() : AppCompatActivity()  {

    val list = arrayListOf<produtosEntidade>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.controlepedidos.R.layout.activity_pagamento)

        // Implement it's on click listener.
        linearLayoutExample.setOnClickListener{onClickCC()}
        //   tvTitle.setOnClickListener { doSomething() }
        linearLayoutDin.setOnClickListener{print()}

       // doPhotoPrint()
        val extras = intent.extras
        val userName: String?

        if (extras != null) {
            userName = extras.getString("valorPagamento")
            valorPagamento.setText(userName)



        }
        if(supportActionBar != null)
        {
            supportActionBar?.hide()
        }

        //image_button1.setOnClickListener{


          //  val intent = Intent(this, FichaActivity::class.java)
        //    intent.putExtra("valorPagamento", valorPagamento.text)
        //    Hawk.put("produtos", list)
        //    startActivity(intent)
       // }
        initList()
    }

    private fun doPhotoPrint() {
        val photoPrinter = PrintHelper(this)
        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.debito)

        photoPrinter.printBitmap("droids.jpg - test print",bitmap)

    }



    private fun initList(){
        Hawk.init(this).build()

        if( !Hawk.contains( "produtos" ) ){
            Hawk.put("produtos", list)
        }

        list.addAll( Hawk.get("produtos") )
    }

    private fun onClickCC(){
        val intent = Intent(this, FichaActivity::class.java)
        intent.putExtra("valorPagamento", valorPagamento.text)
        Hawk.put("produtos", list)
        startActivity(intent)
    }

    private fun print(){
        val intent = Intent(this, PrintDialogActivity::class.java)
        intent.putExtra("valorPagamento", valorPagamento.text)
        Hawk.put("produtos", list)
        startActivity(intent)
    }

}
