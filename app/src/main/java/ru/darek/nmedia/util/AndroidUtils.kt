package ru.darek.nmedia.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun getStrCnt(inCnt:Int):String{
        if (inCnt >= 1000000) return String.format("%.2f M",(inCnt/1000000).toFloat())
        if (inCnt >= 10000) return String.format("%d K",inCnt/1000)
        if (inCnt >= 1000) return String.format("%.1f K",(inCnt/1000).toFloat())
        return inCnt.toString()
    }
}


