package com.dubedivine.samples.util

import android.app.Activity
import android.content.Context
import android.support.v7.widget.CardView
import android.widget.Button
import android.widget.ImageView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.robertlevonyan.views.chip.Chip
import timber.log.Timber

/**
 * Created by divine on 2017/09/12.
 */
object BasicUtils {

   private val _3dp = ViewUtil.dpToPx(3)  // what do u think is it cool ? instead of ThreeDp or i could do this val `3dp` = "3dp"
    fun createTheStatusTextViewInfo(question: Question): String {
        return if (question.answers?.size != null) {
            Timber.d("the answer is: ${question.answers}")
            "answers ${(question.answers?.size)}, answered by ${question.user?.name}"
        } else {
            "answers 0"
        }
    }

    fun createTagsChip(context: Context, chipText: String): Chip {
        val params = ViewUtil.getLayoutParamsForView()
        params.setMargins(_3dp, 0, _3dp, 0)
        val chip = Chip(context)
//        chip.setMargin(_3dp,0, _3dp, 0)
        chip.layoutParams = params
        chip.chipText = "#$chipText" //apend the hash tag yoh!!
        chip.setRandomColor()
        chip.setDefaultDrawableIcon()
        return chip
    }

    fun getFileViewInstance(context: Activity, file: Media, onButtonClick: (media: Media) -> Unit,
                            onCloseButtonClick: (thisView: Button) -> Unit): CardView {
        val cardView = context.layoutInflater.inflate(R.layout.view_file, null) as CardView
        val btnFile = cardView.findViewById<Button>(R.id.btn_show_files)
        val closeButton = cardView.findViewById<ImageView>(R.id.view_file_btn_close)
        btnFile.text = file.name
        when (file.type) {
            Media.PICTURE_TYPE -> {
                btnFile.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_image_black_24dp, 0, 0, 0)
            }
            Media.PICTURE_TYPE -> {
                btnFile.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_ondemand_video_24dp, 0, 0, 0)
            }
            Media.DOCS_TYPE -> {
                btnFile.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_attach_file_24dp, 0, 0, 0)
            }

        }

        btnFile.setOnClickListener({
            onButtonClick(file)
        })
        closeButton.setOnClickListener({
            onCloseButtonClick(btnFile)
        })
        return cardView
    }


}