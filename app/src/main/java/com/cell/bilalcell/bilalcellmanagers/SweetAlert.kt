package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

class SweetAlert {

    fun sweetAlertDialog(conx: Context, Title: String, Content: String, Type: Int, ConfermText: String): SweetAlertDialog {
        val sweet = SweetAlertDialog(conx, Type)
        sweet.titleText = Title
        sweet.contentText = Content
        sweet.confirmText = ConfermText
        return sweet
    }


    fun sweetAlertConf(conx: Context, Title: String, Content: String, Type: Int, ConfermText: String, cancelText: String): SweetAlertDialog {
        val sweet = SweetAlertDialog(conx, Type)
        sweet.titleText = Title
        sweet.contentText = Content
        sweet.confirmText = ConfermText
        sweet.cancelText = cancelText
        return sweet
    }
}