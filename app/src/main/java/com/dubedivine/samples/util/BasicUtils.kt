package com.dubedivine.samples.util

import android.app.Activity
import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.robertlevonyan.views.chip.Chip
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.util.regex.Pattern

/**
 * Created by divine on 2017/09/12.
 */
object BasicUtils {

    private const val REGEX = "#(\\d*[A-Za-z_]+\\w*)\\b(?!;)"


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
                            onCloseButtonClick: (thisView: CardView) -> Unit): CardView {
        val cardView = inflateFor<CardView>(context, R.layout.view_file)
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
            onCloseButtonClick(cardView)
        })
        return cardView
    }

    fun getImagePreviewInstance(context: Activity, path: String,
                                onCloseImageView: (layout: RelativeLayout) -> Unit ) : RelativeLayout {
        val relativeLayout = inflateFor<RelativeLayout>(context, R.layout.view_image)
        val imageView = relativeLayout.findViewById<ImageView>(R.id.view_image_preview)
        val btnCloseImage = relativeLayout.findViewById<ImageView>(R.id.btn_image_close)

        btnCloseImage.setOnClickListener(
                {
                    onCloseImageView(relativeLayout)
                }
        )
        Glide.with(imageView.context)
                .load(path)
                .into(imageView)
        return relativeLayout

    }


    // no need to export this if i need it will just use the  context... as X in the activity
   private fun <T : View> inflateFor(context: Activity, @LayoutRes layout: Int) : T {
       return context.layoutInflater.inflate(layout, null) as T
   }

    fun getCleanTextAndTags(text: String): Pair<String, Set<String>> {
        println("the text is $text")
        val p = getPattern()
        val tags = mutableSetOf<String>()
        val sequenceOfTags = p.toRegex().findAll(text).distinct()
        sequenceOfTags.forEach {
            val tag = it.value.substringAfter('#')
            println("yeye thete are tags $tag ")
            tags.add(tag)
        }
        val  cleanSearchText =  p.matcher(text).replaceAll(" ")
        println("these are the tags fam $tags")
        return Pair(cleanSearchText, tags)
    }

    private  fun getPattern(): Pattern = Pattern.compile(REGEX)

    private fun getMimeType(image: String): String {
        val indexOf = image.lastIndexOf(".") + 1
        var s = image.substring(indexOf)
        if (s == "jpg") {
            s = "jpeg"
        }
        return "image/" + s
    }

    fun createMultiPartFromFile(uris: List<String>): MutableList<MultipartBody.Part>? {
        val multiPartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (imageUri in uris) {
            Log.d("BASIC_UTIL_MIME", "The file name is $uris")
            val file = File(imageUri)
            val mime = getMimeType(imageUri)
            Log.d("BASIC_UTIL_MIME", "the mime is " + mime)
            val reqFile = RequestBody.create(MediaType.parse(mime), file)
            //                 MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), reqFile);
            multiPartBuilder.addFormDataPart("files", file.name, reqFile)
        }
        val body: MultipartBody = multiPartBuilder.build()
        return body.parts()
    }
}