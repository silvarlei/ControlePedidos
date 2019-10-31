package com.example.recycleview_click_listener

import android.view.View

interface ItemClickListener {
    fun onClick(view: View, position: Int)
}