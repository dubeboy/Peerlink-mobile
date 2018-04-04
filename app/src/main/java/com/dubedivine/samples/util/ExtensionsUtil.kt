package com.dubedivine.samples.util

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.dubedivine.samples.BuildConfig
import com.dubedivine.samples.R
import com.dubedivine.samples.R.id.swipe_to_refresh
import com.robertlevonyan.views.chip.Chip
import kotlinx.android.synthetic.main.content_error_and_progress_view.*
import kotlinx.android.synthetic.main.content_swipe_refresh.*
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


// below 4 show just be 2 from the context class

fun Activity.toast(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, duration).show()
}

fun Fragment.toast(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this.context, msg, duration).show()
}

fun Activity.snack(msg: String,  duration: Int = Snackbar.LENGTH_LONG ) {
    ViewUtil.hideKeyboard(this)  // hide the keyboard first
    Snackbar.make(findViewById<View>(android.R.id.content), msg, duration ).show()
}

fun Fragment.snack(msg: String,  duration: Int = Snackbar.LENGTH_LONG ) {
    ViewUtil.hideKeyboard(this.activity)  // hide the keyboard first
    Snackbar.make(view!!, "", duration).show()
}

fun View.removeFromParent() {
    if (parent != null) {
        (this as ViewGroup).removeView(this)
    }
}

//todo: should replicate to create a progress dialog
fun Activity.createAlertDialog(title: String, msg: String? = null, @LayoutRes resource: Int? = null): AlertDialog {
    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setTitle(title)
    alertDialogBuilder.setMessage(msg)
    if (resource != null) {
        val view =  layoutInflater.inflate(resource, null)
        alertDialogBuilder.setView(view)
    }
    return alertDialogBuilder.create()
}

// can be replaced with a snack with a progress loader
@Deprecated("we should find better ui to replace this")
fun Activity.getProgressBarInstance(title: String, msg: String): ProgressDialog {
    val prog = ProgressDialog(this)
    prog.setMessage(msg)
    prog.setTitle(title)
    return prog
}

fun Activity.showProgressAlertDialog(title: String, msg: String? = null, @LayoutRes resource: Int? = null) {
    this.createAlertDialog(title, msg, null)
}

//my timber!!
fun Any.log(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(this.javaClass.simpleName, message)
    }
}