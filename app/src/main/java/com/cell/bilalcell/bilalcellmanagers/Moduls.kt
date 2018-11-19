package com.cell.bilalcell.bilalcellmanagers

import android.net.Uri
import java.net.URI
import java.net.URL

data class CompanyItems(var Name :String,var Logo:Int)


data class clients(var id: String,
                   var username: String,
                   var mobile: String)

data class products(var CompanyName: String, var CountPayments: String, var FirstPayment: String
                    , var ProductName: String, var ProductPrice: String,var Date:String,var time:String)


data class card_services(var image:Int,var name:String,var type_serv:String,
                         var val_price:String,var key_mb:String,var val_mb:String,var key_time:String,
                         var val_time:String,var val_auto:String,var val_msg:String,var val_send_to:String)
