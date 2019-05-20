package com.cell.bilalcell.bilalcellmanagers


data class CompanyItems(var Name: String,
                        var Logo: Int)


data class Clients(var ID: Long,
                   var Name: String,
                   var PhoneNumber: String) {
    constructor() : this(0, "", "")
}


data class Products(var CompanyName: String,
                    var CountPayments: String,
                    var FirstPayment: String,
                    var ProductName: String,
                    var ProductPrice: String,
                    var Date: String,
                    var time: String,
                    val isDone: String)


data class services(var image: Int,
                    var name: String,
                    var type_serv: String,
                    var val_price: String,
                    var key_mb: String,
                    var val_mb: String,
                    var key_time: String,
                    var val_time: String,
                    var val_auto: String,
                    var val_msg: String,
                    var val_send_to: String)


data class Payment(var cash: String,
                   var desc: String,
                   var date: String,
                   var time: String)

data class Note(var title: String,
                var date: String,
                var id: String)

data class News(var id: Int,
                var type: String,
                var name: String,
                var date: String,
                var time: String,
                var desc: String)