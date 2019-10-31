package com.example.controlepedidos.Ficha

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.print.PrintHelper
import android.text.TextWatcher
import android.widget.Toast
import com.example.controlepedidos.*
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_ficha.*
import kotlinx.android.synthetic.main.activity_lista_produtos.*
import kotlinx.android.synthetic.main.activity_pagamento.*
import java.lang.Double
import java.text.NumberFormat
import java.util.*
import com.example.controlepedidos.BluetoothPrinter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.util.Log
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


class FichaActivity : AppCompatActivity() {

    private lateinit var btPrint: BtPrint
    val list = arrayListOf<produtosEntidade>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha)

        val salarioMask = Mask.monetario(txtValorRecebido, txttroco, valorPagamentoFinal)
        txtValorRecebido.addTextChangedListener(salarioMask)
        //val mLocale = Locale("pt", "BR")
        //txtValorRecebido.addTextChangedListener(MoneyTextWatcher(txtValorRecebido, mLocale))
        // txtValorRecebido.addTextChangedListener(

        //  MaskEditUtil.mask( txttroco ,valorPagamentoFinal,txtValorRecebido , MaskEditUtil.FORMAT_Money)
        // )


        val extras = intent.extras
        val userName: String?

        if (extras != null) {
            userName = extras.getString("valorPagamento")
            valorPagamentoFinal.setText(userName)

        }
        initList()

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }





        btPrint = BtPrint(printSwitch, printLoading, printInfo, transactionButton)
        transactionButton.setOnClickListener {

            var strPrint = "**** PEDIDO ****\n"
            //btPrint.doPrint(android.os.Build.BRAND + "\n\n\n")
            for (i in list) {
                strPrint = strPrint + i.descricao + "\n"
                strPrint = strPrint + "*****************\n"
            }

            // We do socket connect here so we can do some handling if something happen with the printer before
            // the actual printing.


            btPrint.socketConnect { result ->

                if (result["success"] == false) {

                    this.runOnUiThread {

                        printInfo.text = result["text"].toString()
                        printSwitch.isChecked = false

                        Toast.makeText(this, "OOPS!!!", Toast.LENGTH_SHORT).show()

                        // TODO: Pooling?

                    }

                } else {

                    try {

                        val bmp = BitmapFactory.decodeResource(
                            resources,
                            R.mipmap.beer
                        )
                        if (bmp != null) {
                            val command = Utils.decodeBitmap(bmp)
                            //outputStream.write(PrinterCommands.ESC_ALIGN_CENTER)
                            //printText(command)

                            btPrint.printTEXT(command)
                        } else {
                            Log.e("Print Photo error", "the file isn't exists")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("PrintTools", "the file isn't exists")
                    }




                    // BluetoothPrinter().printImage(bitmap)
                    // btPrint.doPrint(android.os.Build.BRAND + "\n\n\n")

                    //btPrint.doPrint(i.descricao+ "\n")
                    // btPrint.doPrint("*****************" + "\n")


                    // I'll share how I handle printing format for regular receipts next... :)

                }
            }
        }
    }
        private fun initList() {
            Hawk.init(this).build()

            if (!Hawk.contains("produtos")) {
                Hawk.put("produtos", list)
            }

            list.addAll(Hawk.get("produtos"))
        }

}

