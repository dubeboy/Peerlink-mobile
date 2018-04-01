package com.dubedivine.samples.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dubedivine.samples.BuildConfig
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.detail.DetailPresenter
import com.dubedivine.samples.features.detail.dialog.ShowFullImage
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
    private const val TAG = "__BasicUtils"


    private val threeDp = ViewUtil.dpToPx(3)  // what do u think is it cool ? instead of ThreeDp or i could do this val `3dp` = "3dp"
    fun createTheStatusTextViewInfo(question: Question): String {
        return if (question.answers?.size != null) {
            Timber.d("the answer is: ${question.answers}")
            "answers ${(question.answers?.size)}, answered by ${question.user?.nickname}"
        } else {
            "answers 0"
        }
    }

    fun createTagsChip(context: Context, chipText: String): Chip {
        val params = ViewUtil.getLayoutParamsForView()
        params.setMargins(threeDp, 0, threeDp, 0)
        val chip = Chip(context)
        chip.layoutParams = params
        chip.chipText = "#$chipText" //apend the hash tag yoh!!
        chip.setRandomColor()
        chip.setDefaultDrawableIcon()
        return chip
    }

    fun getFileViewInstance(
            context: Activity,
            file: Media,
            onFileViewButtonClick: (media: Media) -> Unit,
            onCloseButtonClick: (thisView: CardView) -> Unit, showCloseButton: Boolean = true): CardView {

        val cardView = inflateFor<CardView>(context, R.layout.view_file)
        val btnFile = cardView.findViewById<Button>(R.id.btn_show_files)
        val closeButton = cardView.findViewById<ImageView>(R.id.view_file_btn_close)
        btnFile.text = file.name
        when (file.type) {

            Media.PICTURE_TYPE -> {
                //todo: since all of
                btnFile.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_image_black_24dp, 0, 0, 0)
            }
            Media.VIDEO_TYPE -> {
                btnFile.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_ondemand_video_24dp, 0, 0, 0)
            }
            Media.DOCS_TYPE -> {
                btnFile.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_attach_file_24dp, 0, 0, 0)
            }

        }

        if (!showCloseButton) {
            closeButton.visibility = View.GONE
        }

        btnFile.setOnClickListener({
            onFileViewButtonClick(file)
        })
        closeButton.setOnClickListener({
            onCloseButtonClick(cardView)
        })
        return cardView
    }

    fun textHasNoTags(text: String): Boolean {
        return getCleanTextAndTags(text).second.isEmpty()
    }

    fun getImagePreviewInstance(context: Activity, path: String,
                                onCloseImageView: (layout: RelativeLayout) -> Unit): RelativeLayout {
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
    private fun <T : View> inflateFor(context: Activity, @LayoutRes layout: Int): T {
        return context.layoutInflater.inflate(layout, null) as T
    }

    fun getCleanTextAndTags(text: String): Pair<String, Set<String>> {
        Log.d(TAG, "the text is: $text")
        val p = getPattern()
        val tags = mutableSetOf<String>()
        val sequenceOfTags = p.toRegex().findAll(text).distinct()
        sequenceOfTags.forEach {
            val tag = it.value.substringAfter('#')
            println("yeye thete are tags $tag ")
            tags.add(tag)
        }
        val cleanSearchText = p.matcher(text).replaceAll(" ")
        println("these are the tags fam $tags")
        return Pair(cleanSearchText, tags)
    }

    private fun getPattern(): Pattern = Pattern.compile(REGEX)

    private fun getMimeType(image: String): String {
        val indexOf = image.lastIndexOf(".") + 1
        var s = image.substring(indexOf)
        if (s == "jpg") {
            s = "jpeg"
        }
//       ContentResolver cR = context.getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        return "image/$s"
    }

    fun createMultiPartFromFile(uris: List<String>, context: Context? = null): MutableList<MultipartBody.Part> {
        val multiPartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (imageUri in uris) {
            Log.d("BASIC_UTIL_MIME", "The file name is $uris")
            val file = File(imageUri)
            val mime = getMimeType(imageUri)
            Log.d("BASIC_UTIL_MIME", "the mime is $mime")
            val reqFile = RequestBody.create(MediaType.parse(mime), file)
            //                 MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), reqFile);
            multiPartBuilder.addFormDataPart("files", file.name, reqFile)
        }
        val body: MultipartBody = multiPartBuilder.build()
        return body.parts()
    }

    fun getRealPathFromURI(uri: Uri, context: Activity): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val path = cursor.getString(idx)
        cursor.close()
        return path
    }


    fun genMediaFullUrl(mediaID: String): String {
        return BuildConfig.BASE_API_URL + mediaID
    }

    fun checkExternalReadWritePermissions(activity: Activity, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            /*ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)*/
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode)
        }
    }


    fun addPicturesListToHorizontalListView(horizontalScrollView: LinearLayout,
                                            pictures: List<String>?,
                                            context: AppCompatActivity,
                                            mDetailPresenter: DetailPresenter,
                                            enableImageViewClick: Boolean = true) {

        // we wnat it to have 0 children at first
        if (pictures != null) {
            horizontalScrollView.visibility = View.VISIBLE
            if (horizontalScrollView.childCount > 0) horizontalScrollView.removeAllViewsInLayout()
            pictures.forEachIndexed(
                    { i: Int, location: String ->
                        // since its an Item we inflate it many times
                        val constraintLayout = BasicUtils.inflateFor<ConstraintLayout>(context, R.layout.item_fragment_full_image_view)
                        val imageView = constraintLayout.findViewById<ImageView>(R.id.img_full_image_view)
                        val progressBar = constraintLayout.findViewById<ProgressBar>(R.id.progress_image_loading)
                        //change the params so that they give other items space
                        imageView.layoutParams = LinearLayout.LayoutParams(ViewUtil.dpToPx(300), ViewUtil.dpToPx(300))
                        Glide.with(context)
                                .load(BasicUtils.genMediaFullUrl(location))
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                                        progressBar.visibility = View.GONE
                                        return false
                                    }

                                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                        progressBar.visibility = View.GONE
                                        return false
                                    }
                                })
                                .into(imageView)
                        if (enableImageViewClick) {
                            imageView.setOnClickListener({
                                ShowFullImage.newInstance(mDetailPresenter, i, pictures)
                                        .show(context.supportFragmentManager, "showFullImagesFragments")
                            })
                        }
                        horizontalScrollView.addView(constraintLayout)
                    }
            )
        }
    }

}