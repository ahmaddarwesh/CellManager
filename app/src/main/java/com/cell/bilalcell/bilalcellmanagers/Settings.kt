package com.cell.bilalcell.bilalcellmanagers

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val AutoU = Cookies().getInt(this, "AutoUpdate")
        val statRB = Cookies().getString(this, "statRB")

        if (AutoU == 1) {
            myRadioB.isEnabled = true
            RB_1Hour.isEnabled = true
            RB_12Hour.isEnabled = true
            RB_1Day.isEnabled = true
            sw_Update_Auto.isChecked = true
        } else {
            myRadioB.isEnabled = false
            RB_1Hour.isEnabled = false
            RB_12Hour.isEnabled = false
            RB_1Day.isEnabled = false
            sw_Update_Auto.isChecked = false
        }

        when (statRB) {
            "1 Hour" -> {
                RB_1Hour.isChecked = true
            }
            "12 Hour" -> {
                RB_12Hour.isChecked = true
            }
            "1 Day" -> {
                RB_1Day.isChecked = true
            }
        }

        sw_Update_Auto.setOnCheckedChangeListener { buttonView, isChecked ->
            myRadioB.isEnabled = isChecked
            RB_1Hour.isEnabled = isChecked
            RB_12Hour.isEnabled = isChecked
            RB_1Day.isEnabled = isChecked

            if (isChecked) {
                Cookies().SaveInt(this, "AutoUpdate", 1)
            } else {
                Cookies().SaveInt(this, "AutoUpdate", 0)
            }
        }

        myRadioB.setOnCheckedChangeListener { group, checkedId ->
            val rb: RadioButton = group.findViewById(checkedId)
            Cookies().saveString(this, "statRB", "${rb.text}")
            Toast.makeText(this, "Update every ${rb.text} => Enabled", Toast.LENGTH_LONG).show()

        }


    }

}
