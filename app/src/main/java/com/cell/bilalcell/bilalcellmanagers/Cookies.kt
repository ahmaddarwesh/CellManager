package com.cell.bilalcell.bilalcellmanagers

import android.content.Context

class Cookies {

    fun saveString(context:Context,key:String,value:String){
        val shareP = context.getSharedPreferences("cookies",0)
        val editor = shareP.edit()
        editor.putString(key,value)
        editor.apply()

    }

    fun getString(context: Context,key: String):String{
        val shareP = context.getSharedPreferences("cookies",0)
        return  shareP.getString(key,"nullData")
    }
    fun SaveInt(context: Context,key:String,value:Int){
        val shareP = context.getSharedPreferences("cookies",0)
        val editor = shareP.edit()
        editor.putInt(key,value)
        editor.apply()
    }
    fun getInt(context: Context,kye:String):Int{
        val shareP = context.getSharedPreferences("cookies",0)
        return shareP.getInt(kye,0)

    }
}