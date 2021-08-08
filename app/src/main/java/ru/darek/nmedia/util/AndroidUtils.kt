package ru.darek.nmedia.util

class AndroidUtils {




}
fun getStrCnt(inCnt:Int):String{
    if (inCnt >= 1000000) return String.format("%.2f M",(inCnt/1000000).toFloat())
    if (inCnt >= 10000) return String.format("%d K",inCnt/1000)
    if (inCnt >= 1000) return String.format("%.1f K",(inCnt/1000).toFloat())
    return inCnt.toString()
}
