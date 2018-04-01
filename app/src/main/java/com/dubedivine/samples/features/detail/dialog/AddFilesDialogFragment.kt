package com.dubedivine.samples.features.detail.dialog

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dubedivine.samples.R
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.features.addQuestion.AddQuestionActivity
import com.dubedivine.samples.util.BasicUtils.getRealPathFromURI
import com.dubedivine.samples.util.snack
import com.dubedivine.samples.util.toast
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst

/**
 * Created by divine on 2017/10/06.
 */

/*
 *
  * the type: Char is a problem its functionality should be removed its redundant
  * it uses prefs which is no need we should fix this
* */
class AddFilesDialogFragment : DialogFragment() {
    private var mediaFiles: HashMap<Char, List<String>> = HashMap() //Maps media type to Files
    private lateinit var onItemClick: OnItemClick
    private lateinit var btnAttachPhotos: FloatingActionButton
    private lateinit var btnAttachVideos: FloatingActionButton
    private lateinit var btnAttachFiles: FloatingActionButton
    //overkill variable should maintain state within the fragment itself , google Fragment Manager maintain state
    private var type: Char = NO_MEDIA //default type is NO_MEDIA
    private var permissionGranted = true

    private lateinit var pref: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "On create is called")
        val permissionCheckWrite = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionCheckRead = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED ||
                permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                activity.toast("Accept the permissions so that the app can complete its functionality")
            } else {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        }
        //  setStyle(DialogFragment.STYLE_NO_TITLE,  R.style.MyDialog)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    activity.snack("Permission granted.")
                } else {
                    permissionGranted = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionGranted)
            type = pref.getString(CURRENT_MEDIA_TYPE, NO_MEDIA.toString()).first()
        else
            Log.d(TAG, "Permission not grated")

        enableButtonForType(type)

        Log.d(TAG, "On onResume called type $type")
        //todo: there should be a text box that we enable to tell the user to remove the
        //content on the parent
        //  enableButtonsForType(btnAttachPhotos, btnAttachVideos, btnAttachFiles)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "On onPause after called type $type")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.alert_dialog_select_files, container, false)
        pref = PreferencesHelper(context)
//            type = savedInstanceState.getChar(CURRENT_MEDIA_TYPE)
        Log.d(TAG, "onCreateView called and medafile looks like $mediaFiles and type $type")
        if (mediaFiles.isEmpty()) {
            saveTypeToPreferences(NO_MEDIA)
        }

        btnAttachPhotos = view.findViewById(R.id.btn_attach_photos)
        btnAttachVideos = view.findViewById(R.id.btn_attach_video)
        btnAttachFiles = view.findViewById(R.id.btn_attach_filez)

        //  enableButtonsForType(btnAttachPhotos, btnAttachVideos, btnAttachFiles)
        // make dialog itself transparent
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // remove background dim
        dialog.window.setDimAmount(0.3F)

        btnAttachPhotos.setOnClickListener({
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this)
        })

        btnAttachVideos.setOnClickListener({
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (takeVideoIntent.resolveActivity(activity.packageManager) != null) {
                Log.d(TAG, " the data is ${takeVideoIntent.data}")
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


    //todo: these 2 following functions contain similar functionality with the one @AddQuestionActivity
    //this onActivityResult if from the FilePicker and the camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, intent)
            Log.d(TAG, "the result code is $requestCode")
            processTheInputRequest(requestCode, intent)
        } else {
            Log.d(TAG, "the activity did not respond as expected man")
        }
    }

    fun setListener(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    // to be called by the main activity to enable all the buttons ie when there are no selected elements
    fun enableAllButtons() {
        type = NO_MEDIA
        saveTypeToPreferences(type)
    }

    //todo: should factorise
    private fun processTheInputRequest(requestCode: Int, intent: Intent?) {

        Log.d(TAG, "Calling the processTheInputRequest function ")

        when (requestCode) {

            AddQuestionActivity.REQUEST_VIDEO_CAPTURE -> {
                if (intent?.data != null) {
                    val vidUri = intent.data!!
                    val vidUrl = getRealPathFromURI(vidUri, activity)
                    Log.d(TAG, "the path is $vidUrl")
                    mediaFiles = hashMapOf(Media.VIDEO_TYPE to listOf(vidUrl))
                }
            }

            FilePickerConst.REQUEST_CODE_PHOTO -> {
                if (intent != null) {
                    val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                    Log.d(TAG, "the data that we got: Photos $photosPaths")
                    mediaFiles = hashMapOf(Media.PICTURE_TYPE to photosPaths)
//                    mediaFiles[Media.PICTURE_TYPE] = photosPaths
                }
            }

            FilePickerConst.REQUEST_CODE_DOC -> {
                if (intent != null) {
                    val docsPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)
                    Log.d(TAG, "the data that we got $docsPaths")
                    mediaFiles = hashMapOf(Media.DOCS_TYPE to docsPaths)
                }
            }
        }

        val docsListPaths = mediaFiles[Media.DOCS_TYPE]
        val videoListPaths = mediaFiles[Media.VIDEO_TYPE]
        val picturesListPaths = mediaFiles[Media.PICTURE_TYPE]

        when {
            docsListPaths != null && docsListPaths.isNotEmpty() -> {
                onItemClick.onItemClick(docsListPaths, Media.DOCS_TYPE)
                type = Media.DOCS_TYPE
                saveTypeToPreferences(type)
                return
            }
            videoListPaths != null && videoListPaths.isNotEmpty() -> {
                onItemClick.onItemClick(videoListPaths, Media.VIDEO_TYPE)
                type = Media.VIDEO_TYPE
                saveTypeToPreferences(type)
                return
            }
            picturesListPaths != null && picturesListPaths.isNotEmpty() -> {
                Log.d(TAG, "In here bro looking at pictures")
                onItemClick.onItemClick(picturesListPaths, Media.PICTURE_TYPE)
                type = Media.PICTURE_TYPE
                saveTypeToPreferences(type)
                return
            }
            else -> type = NO_MEDIA
        }
        //  enableButtonsForType(btnAttachPhotos, btnAttachVideos, btnAttachFiles)
    }

    private fun enableButtonForType(currentMediaType: Char) {

        Log.d(TAG, "The type is $type")

        when (currentMediaType) {
            Media.PICTURE_TYPE -> {
                Log.d(TAG, "In the pics")
                btnAttachPhotos.isEnabled = true
                btnAttachVideos.isEnabled = false
                btnAttachFiles.isEnabled = false
                return
            }
            Media.VIDEO_TYPE -> {
                Log.d(TAG, "In the vids")
                btnAttachPhotos.isEnabled = false
                btnAttachVideos.isEnabled = true
                btnAttachFiles.isEnabled = false
                return
            }
            Media.DOCS_TYPE -> {
                Log.d(TAG, "In the docs")
                btnAttachPhotos.isEnabled = false
                btnAttachVideos.isEnabled = false
                btnAttachFiles.isEnabled = true
                return
            }
            NO_MEDIA -> {
                btnAttachPhotos.isEnabled = true
                btnAttachVideos.isEnabled = true
                btnAttachFiles.isEnabled = true
            }
        }
    }

    private fun saveTypeToPreferences(type: Char) {
        if (permissionGranted)
            pref.save {
                putString(CURRENT_MEDIA_TYPE, type.toString())
            }
        else
            Log.d(TAG, "Permission not grated -> could not save")
    }

    interface OnItemClick {
        fun onItemClick(fileList: List<String>, type: Char)
    }

    companion object {

        const val TAG = "__AddFilesDialog"
        const val NO_MEDIA = 'N'
        const val CURRENT_MEDIA_TYPE = "mediaType"
        const val MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1003

        fun newInstance(onItemClick: OnItemClick): AddFilesDialogFragment {
            val myFragment = AddFilesDialogFragment()
            myFragment.setListener(onItemClick)
            return myFragment
        }
    }
}
