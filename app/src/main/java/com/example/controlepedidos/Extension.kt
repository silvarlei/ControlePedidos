package com.example.controlepedidos

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context

fun View.visible()
{
    visibility = View.VISIBLE
}

fun View.gone()
{
    visibility = View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}