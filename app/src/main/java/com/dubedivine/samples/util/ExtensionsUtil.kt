package com.dubedivine.samples.util

import android.app.Activity
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.robertlevonyan.views.chip.Chip
import java.time.Duration

/**
 * Created by divine on 2017/09/12.
 */
fun Chip.setRandomColor() {
    val color = Color.parseColor(this.chipText)
    if(Color.BLACK != color) {  // so that we dont have some color conflicts yoh
        this.backgroundColor = color
    } // else leave it to the default color
}

fun Chip.setDefaultDrawableIcon() {
    this.isHasIcon = true
    this.chipIcon = TextDrawable.builder().buildRound(this.chipText ,
            Color.parseColor(this.chipText + "dube")  ) //append a string so that the color is different from the tag color

}


fun Activity.toast(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, duration).show()
}