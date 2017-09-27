package com.dubedivine.samples.util

import android.app.Activity
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.robertlevonyan.views.chip.Chip
import timber.log.Timber

/**
 * Created by divine on 2017/09/12.
 */
fun Chip.setRandomColor() {
    Timber.i("the text the colorHex is ${this.chipText.toHexColor()} and the actual text is :  ${this.chipText.toHexColor()} ")
    val color: Int = Color.parseColor(this.chipText.toHexColor())
    if(Color.BLACK != color) {  // so that we dont have some color conflicts yoh
        this.backgroundColor = color
    } // else leave it to the default color
}

fun String.toHexColor(): String {
    return String.format("#%X", this.hashCode()).slice(0..6)
}

fun Chip.setDefaultDrawableIcon() {
//    this.isHasIcon = true
//    this.chipIcon = TextDrawable.builder().buildRound(this.chipText ,
//            Color.parseColor((this.chipText + "dube").toHexColor())  ) //append a string so that the color is different from the tag color

}


fun Activity.toast(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, duration).show()
}

fun Activity.snack(msg: String,  duration: Int = Snackbar.LENGTH_LONG ) {
    ViewUtil.hideKeyboard(this)  // hide the keyboard first
    Snackbar.make(findViewById<View>(android.R.id.content), msg, duration ).show()
}

fun Fragment.snack(msg: String,  duration: Int = Snackbar.LENGTH_LONG ) {
    ViewUtil.hideKeyboard(this.activity)  // hide the keyboard first
    Snackbar.make(view!!, "", duration).show()
}

