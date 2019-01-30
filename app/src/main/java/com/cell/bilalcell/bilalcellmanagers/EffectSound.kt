package com.cell.bilalcell.bilalcellmanagers

import android.content.Context
import android.media.MediaPlayer


class EffectSound {

    companion object {
        fun touchplay(conx: Context) {
            val mPlayer = MediaPlayer.create(conx, R.raw.touch)
            mPlayer.start()
        }
    }

}