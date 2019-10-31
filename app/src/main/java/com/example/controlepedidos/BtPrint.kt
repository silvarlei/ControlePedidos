package com.example.controlepedidos


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Bitmap
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import android.support.v4.app.NotificationCompat.getExtras
import java.io.ByteArrayOutputStream
import android.system.Os.socket
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Log
import java.io.DataOutputStream


class BtPrint(

    private var printSwitch: Switch,
    private var printLoading: ProgressBar,
    private var printInfo: TextView,
    private var printButton: Button

) {


    // Define the caller context and activity (tips on optimization will be much appreciated :)

    private val context = printSwitch.context
    private val activity = context as Activity
    private val sharedPrefs = context.getSharedPreferences(context.packageName + ".META", Context.MODE_PRIVATE)


    // Other initializer

    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var printers = ArrayList<BluetoothDevice>()

    private lateinit var printer: BluetoothDevice
    private lateinit var socket: BluetoothSocket


    init {


        // Set Views to initial value.

        preCheckStart()


        // Only proceed if there's a record in sharedPrefs...

        if (sharedPrefs.getString("lastPrinter", "") != "") {

            preCheck()

        } else {


            // ...to skip (second) auto connection attempt if no printers active...

            printInfo.text = "Não vai imprimir"
            preCheckDone()

        }


        // ...but of course proceed if switched manually.

        printSwitch.setOnClickListener {

            if (printSwitch.isChecked) {

                preCheck()

            } else {

                printInfo.text = "Não vai imprimir"

            }

        }

    }


    private fun preCheck() {

        preCheckStart()


        // No bluetooth

        if (bluetoothAdapter == null) {

            printInfo.text = "Esse celular não tem bluetooth"
            printSwitch.isChecked = false
            preCheckDone()

        } else {


            // Bluetooth inactive

            if (!bluetoothAdapter.isEnabled) {

                printInfo.text = "Bluetooth inativo"
                printSwitch.isChecked = false
                preCheckDone()

            } else {


                // Bluetooth available and active, so refresh printers list.

                refreshPrinters()

                if (printers.size > 0) {


                    // Loop the printers to crosscheck with sharedPrefs, and also to prepare arrays for printer selection
                    // dialog if needed.

                    val pNames = Array(printers.size) { "" }
                    val pAddrs = Array(printers.size) { "" }

                    var deviceFound = false

                    for (i in 0 until printers.size) {

                        pNames[i] = printers[i].name
                        pAddrs[i] = printers[i].address // How to do this "correctly" in Kotlin? :D


                        // Printer available in sharedPrefs, attempt connection and break.

                        if (printers[i].address == sharedPrefs.getString("lastPrinter", "")) {

                            deviceFound = true
                            printer = printers[i]
                            testConnection()
                            break

                        }

                    }


                    // If it gets here

                    if (!deviceFound) {


                        // Show printer selection dialog

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Selecione uma Impressora")
                            .setItems(
                                pNames
                            ) { _, which ->


                                // On selected, save and rerun preCheck()

                                sharedPrefs.edit().putString("lastPrinter", pAddrs[which]).apply()
                                preCheck()

                            }
                        builder.create()
                        builder.setCancelable(false)
                        builder.show()

                    }

                } else {


                    // No printers

                    printInfo.text = "Por favor pareie uma impressora"
                    printSwitch.isChecked = false
                    preCheckDone()

                }

            }

        }

    }


    private fun refreshPrinters() {


        // Clean up first, but I found out that sometimes the device must be reset, and/or Clear Data in
        // Bluetooth Share app, to really refresh the paired devices list, after we Forget/Add a bluetooth device.
        // This probably a device issue (or just me lacking experience).

        printers.clear()


        // Filter the paired devices for printers

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() // this is my attempt to "really refresh" the list

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices

        if (pairedDevices != null) {

            for (i in pairedDevices) {

                if (i.bluetoothClass.deviceClass.toString() == "1664") { // Identifier (or something) for printers.


                    // Now we have a list of printers

                    printers.add(i)

                }

            }

        }

    }


    private fun testConnection() {


        // Make sure discovery isn't running

        bluetoothAdapter?.cancelDiscovery()


        // Socket connect will freeze the UI, so we're doing this in another thread and get the callback

        printInfo.text = printer.name
        printInfo.append("... ")

        socketConnect { result ->


            // Other threads can't touch UI without runOnUiThread()

            activity.runOnUiThread {

                printInfo.append(result["text"].toString())


                // Connection failed, delete from sharedPrefs

                if (result["success"] == false) {

                    sharedPrefs.edit().putString("lastPrinter", "").apply()
                    printSwitch.isChecked = false

                } else {

                    printSwitch.isChecked = true

                }

                preCheckDone()

            }


            // Done checking. From here we assume the printer is active, nearby, and is ready for real printing.
            // We close the socket to allow other devices to use the printer although it might require some more coding
            // to imitate some kind of pooling service (no such service in my two test/cheap printers here).

            socket.close()

        }

    }


    fun socketConnect(callback: (HashMap<String, Any>) -> Unit) {


        // Make sure socket is closed

        if (::socket.isInitialized) socket.close()


        // Google for explanation on this :D

        socket = printer.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))

        thread(start = true) {

            val result = HashMap<String, Any>()

            try {
                socket.connect()
                result["success"] = true
                result["text"] = "connected."
            } catch (e: IOException) {
                result["success"] = false
                result["text"] = e
            }

            callback(result)

        }

    }


    private fun preCheckStart() {

        printLoading.visibility = View.VISIBLE
        printButton.isClickable = false
        printSwitch.alpha = .25f

    }

    private fun preCheckDone() {

        printLoading.visibility = View.INVISIBLE
        printButton.isClickable = true
        printSwitch.alpha = 1f

    }


    fun doPrint(stringToPrint: String, keepSocket: Boolean = false) {


        // ESC/POS default format

        socket.outputStream.write(byteArrayOf(27, 33, 0))


        // Print your string

        socket.outputStream.write(stringToPrint.toByteArray())


        if (!keepSocket) {
            socket.close()
        }

    }



    fun doPrintImage(bitmap: Bitmap) {


        val mPrinter = BluetoothPrinter(printers[0])
        mPrinter.connectPrinter(object : BluetoothPrinter.PrinterConnectListener {

            override fun onConnected() {

                // mPrinter.printText("Hello World!")


                mPrinter.printImage(bitmap)
                mPrinter.addNewLine()
                mPrinter.addNewLine()
                mPrinter.finish()
            }


            override fun onFailed() {
                Log.d("BluetoothPrinter", "Conection failed")
            }

        })

    }

    fun printTEXT(byte: ByteArray) {

        val mPrinter = BluetoothPrinter(printers[0])
        mPrinter.connectPrinter(object : BluetoothPrinter.PrinterConnectListener {

            override fun onConnected() {

                // mPrinter.printText("Hello World!")


                //mPrinter.printBity(byte)
                mPrinter.printCustom("Fair Group BD",2,1);
                mPrinter.printCustom("Pepperoni Foods Ltd.",0,1);

                mPrinter.printCustom("H-123, R-123, Dhanmondi, Dhaka-1212",0,1);
                mPrinter.printCustom("Hot Line: +88000 000000",0,1);
                mPrinter.printCustom("Vat Reg : 0000000000,Mushak : 11",0,1);



                mPrinter.printCustom("Thank you for coming & we look",0,1);
                mPrinter.printCustom("forward to serve you again",0,1);
                mPrinter.addNewLine()
                mPrinter.addNewLine()
                mPrinter.printLine()
                mPrinter.finish()
            }


            override fun onFailed() {
                Log.d("BluetoothPrinter", "Conection failed")
            }

        })



    }
}