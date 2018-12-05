package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog

public class SweetAlert {

    fun sweetAlertDialog(conx:Context,Title: String, Content: String, Type: Int, ConfermText: String): SweetAlertDialog {
        val s = SweetAlertDialog(conx, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText

        return s
    }

      fun sweetAlertConf(conx:Context,Title: String, Content: String, Type: Int, ConfermText: String, cancelText: String): SweetAlertDialog {
        val s = SweetAlertDialog(conx, Type)
        s.titleText = Title
        s.contentText = Content
        s.confirmText = ConfermText
        s.cancelText = cancelText
        return s
    }
}