package com.dubedivine.samples.features.detail.dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.features.addQuestion.AddQuestionActivity
import com.dubedivine.samples.util.BasicUtils.getRealPathFromURI
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst

/**
 * Created by divine on 2017/10/06.
 */

class AddFilesDialogFragment : DialogFragment() {


    private var mediaFiles: HashMap<Char, List<String>>? = null //Maps media type to Files
    private lateinit var onItemClick: OnItemClick
    private lateinit var btnAttachPhotos: FloatingActionButton
    private lateinit var btnAttachVideos: FloatingActionButton
    private lateinit var btnAttachFiles: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setStyle(DialogFragment.STYLE_NO_TITLE,  R.style.MyDialog)
    }

    override fun onResume() {
        super.onResume()
        if (!parentHasChildren()) {
            mediaFiles?.clear()
        }
        //todo: there should be a text box that we enable to tell the user to remove the
        //content on the parent
        enableButtons(btnAttachPhotos, btnAttachVideos, btnAttachFiles)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.alert_dialog_select_files, container, false)

        btnAttachPhotos = view.findViewById<FloatingActionButton>(R.id.btn_attach_photos)
        btnAttachVideos = view.findViewById<FloatingActionButton>(R.id.btn_attach_video)
        btnAttachFiles = view.findViewById<FloatingActionButton>(R.id.btn_attach_filez)


        // make dialog itself transparent
        getDialog().window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // remove background dim
        getDialog().window.setDimAmount(0.2F)

        btnAttachPhotos.setOnClickListener({
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this)
        })

        btnAttachVideos.setOnClickListener({
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (takeVideoIntent.resolveActivity(activity.packageManager) != null) {
                Log.d(AddQuestionActivity.TAG, " the data is ${takeVideoIntent.data}")
                startActivityForResult(takeVideoIntent, AddQuestionActivity.REQUEST_VIDEO_CAPTURE)
            }
        })

        btnAttachFiles.setOnClickListener({
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.AppTheme)
                    .pickFile(this)
        })
        return view
    }


    //todo: these 2 following function contain similar functionality with the one @AddQuestionActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, intent)
            Log.d(TAG, "the result code is $requestCode")
            processTheInputRequest(requestCode, intent)
        } else {
            Log.d(TAG, "the activity did ot respond as axpected man")
        }
    }

    //todo: should factorise
    private fun processTheInputRequest(requestCode: Int, intent: Intent?) {


        when (requestCode) {

            AddQuestionActivity.REQUEST_VIDEO_CAPTURE -> {
                if (intent?.data != null) {
                    val vidUri = intent.data!!
                    val vidUrl = getRealPathFromURI(vidUri, activity)
                    Log.d(AddQuestionActivity.TAG, "the path is $vidUrl")
                    mediaFiles = hashMapOf(Media.VIDEO_TYPE to listOf(vidUrl))
                }
            }

            FilePickerConst.REQUEST_CODE_PHOTO -> {
                if (intent != null) {
                    val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                    Log.d(AddQuestionActivity.TAG, "the data that we got: Photos $photosPaths")
                    mediaFiles = hashMapOf(Media.PICTURE_TYPE to photosPaths)
                }
            }

            FilePickerConst.REQUEST_CODE_DOC -> {
                if (intent != null) {
                    val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)
                    Log.d(AddQuestionActivity.TAG, "the data that we got ${intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)}")
                  mediaFiles = hashMapOf(Media.DOCS_TYPE to photosPaths)
                }
            }
        }

        val docsListPaths = mediaFiles?.get(Media.DOCS_TYPE)
        val videoListPaths = mediaFiles?.get(Media.VIDEO_TYPE)
        val picturesListPaths = mediaFiles?.get(Media.PICTURE_TYPE)

        when {
            docsListPaths != null && docsListPaths.isNotEmpty() -> {
                onItemClick.onItemClick(docsListPaths, Media.DOCS_TYPE)
                return
            }
            videoListPaths != null && videoListPaths.isNotEmpty() -> {
                onItemClick.onItemClick(videoListPaths, Media.VIDEO_TYPE)
                return
            }
            picturesListPaths != null && picturesListPaths.isNotEmpty() -> {
                Log.d(TAG, "In here bro looking at this bra")
                onItemClick.onItemClick(picturesListPaths, Media.PICTURE_TYPE)
                return
            }
        }

    }

    private fun enableButtons(fabPic: FloatingActionButton, fabVid: FloatingActionButton, fabDoc: FloatingActionButton) {

        val docsListPaths = mediaFiles?.get(Media.DOCS_TYPE)
        val videoListPaths = mediaFiles?.get(Media.VIDEO_TYPE)
        val picturesListPaths = mediaFiles?.get(Media.PICTURE_TYPE)

        when {
            picturesListPaths != null && picturesListPaths.isNotEmpty() -> {
                fabPic.isEnabled = true
                fabVid.isEnabled = false
                fabDoc.isEnabled = false
            }
            videoListPaths != null && videoListPaths.isNotEmpty() -> {
                fabPic.isEnabled = false
                fabVid.isEnabled = true
                fabDoc.isEnabled = false
            }
            docsListPaths != null && docsListPaths.isNotEmpty() -> {
                fabPic.isEnabled = false
                fabVid.isEnabled = false
                fabDoc.isEnabled = true
            }
            else -> {
                fabPic.isEnabled = true
                fabVid.isEnabled = true
                fabDoc.isEnabled = true
            }
        }
    }



    fun setListener(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    interface OnItemClick {
        fun onItemClick(fileList: List<String>, type: Char)
    }

    fun setResumeArguments(parentHasAnyChildren: Boolean) {
        arguments.putBoolean(PARENT_HAS_ANY_CHILDREN, parentHasAnyChildren)
    }

    private fun parentHasChildren(): Boolean {
      return (arguments != null && arguments.getBoolean(PARENT_HAS_ANY_CHILDREN))
    }


    // should be able to pass back the data to the activoty

    companion object {
        val TAG = "__AddFilesDialog"
        val PARENT_HAS_ANY_CHILDREN = "has_any_children"
        fun newInstance(parentHasAnyChildren: Boolean, onItemClick: OnItemClick): AddFilesDialogFragment {
            val myFragment = AddFilesDialogFragment()
            val args = Bundle()
            args.putBoolean(PARENT_HAS_ANY_CHILDREN, parentHasAnyChildren)
            myFragment.arguments = args
            myFragment.setListener(onItemClick)
            return myFragment
        }
    }
}
