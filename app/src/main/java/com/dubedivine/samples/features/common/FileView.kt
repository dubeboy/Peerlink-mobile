package com.dubedivine.samples.features.common

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import timber.log.Timber


//todo this class has very deep nesting but its okay for now
// this class might have been an over kill though could have just created the class and added
// we are constructing
// linearLayout + CardView + Button instead of just a Button mhhh...
/**
 * Created by divine on 2017/09/11.
 */

//todo: should remove the card view parent of this View
class FileView : LinearLayout {

    private var onFileClickedListener: OnFileClickedListener? = null
    private var file: Media? = null
    @BindView(R.id.btn_show_files) @JvmField var btnFile: Button? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }


    // with this constructor the attributes are set using the XML bro
    constructor(context: Context, file: Media) : super(context) {
        init()
        this.file = file
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    @OnClick(R.id.btn_show_files)
    fun onFileClick() {
        if (onFileClickedListener != null && file != null) {
            onFileClickedListener?.onFileClick(file)
        } else {
            Timber.e("oops dwag the the file is null or the click listener is not set ")
            // not sure if its a good idea
            throw IllegalStateException("file is null or the click listener is not set")
        }
    }

    private fun init() {
        orientation = LinearLayout.HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.view_file, this)
        ButterKnife.bind(this)
        if(file != null ) {
            btnFile!!.text = file!!.name
            when(file!!.type) {
                Media.PICTURE_TYPE -> {
                    btnFile!!.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_image_black_24dp, 0, 0 , 0)
                }
                Media.PICTURE_TYPE -> {
                    btnFile!!.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_ondemand_video_24dp, 0, 0, 0)
                }
                Media.DOCS_TYPE -> {
                    btnFile!!.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_attach_file_24dp, 0, 0, 0)
                }

            }

        }

    }

     fun setOnFileClickedListener(onFileClickedListener: OnFileClickedListener ) {
        this.onFileClickedListener = onFileClickedListener
     }

    interface OnFileClickedListener {
        fun onFileClick(file: Media?);
    }
}