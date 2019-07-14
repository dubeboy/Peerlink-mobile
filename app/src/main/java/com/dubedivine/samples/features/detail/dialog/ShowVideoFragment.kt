package com.dubedivine.samples.features.detail.dialog

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.dubedivine.samples.R
import com.dubedivine.samples.features.detail.DetailPresenter
import com.dubedivine.samples.util.BasicUtils
import java.io.File
import java.io.FilenameFilter
import kotlin.math.roundToInt

class ShowVideoFragment : BottomSheetDialogFragment() {

    private var videoLocation: String? = null
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoLocation = it.getString(VIDEO_LOCATION)
        }
        BasicUtils.checkExternalReadWritePermissions(activity!!, 100)
    }

    // totally overring the super method
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_video, container, false)
        val videoView: VideoView = view.findViewById(R.id.video_view)
        progressBar = view.findViewById(R.id.progress_vid_loading)
        progressBar.max = 100
        val errorText: TextView = view.findViewById(R.id.error_text)
        val mediaController = MediaController(view.context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (metrics.heightPixels * 0.10).roundToInt())


        //get the root external DIR
        val externalFilesDir = activity!!.getExternalFilesDir(null)
        val peerlinkDirectory = externalFilesDir.absolutePath

        videoView.setOnPreparedListener({
            // mediaController.show(2000)
        })
        val path = fileDownloaded(externalFilesDir, videoLocation!!)
        if (path.isNotBlank()) {
            val file = File(externalFilesDir, path)
            showVideo(videoView, file)
        } else {
            mDetailPresenter.fragmentFetchVideo(peerlinkDirectory, videoLocation!!,
                    { isSuccess: Boolean, message: String, file: File? ->
                        if (isSuccess) {
                            showVideo(videoView, file!!)
                        } else {
                            // close the fragment
                            errorText.visibility = View.VISIBLE
                            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                        }
                    },
                    { prog: Long, fileSize: Long ->
                        //                    val inc = ((prog.toDouble() / fileSize.toDouble()) * 100).toInt()
                        //                    Log.d(TAG, "the progress $prog and fileSize $fileSize and inc is $inc")
                        //                    progressBar.incrementProgressBy( inc )
                    })
        }

        return view
    }

    private fun fileDownloaded(root: File, videoLocation: String): String {
        for (filename: String in root.list()) {
            if (filename.substringBefore("_") == videoLocation)
                return filename
        }
        return ""
    }


    private fun showVideo(videoView: VideoView, file: File) {
        videoView.visibility = View.VISIBLE
        videoView.requestFocus()
        videoView.setVideoURI(Uri.parse(file.absolutePath))
        progressBar.visibility = View.GONE
        videoView.start()
    }


    companion object {
        lateinit var mDetailPresenter: DetailPresenter

        private const val VIDEO_LOCATION = "video_location"
        private const val TAG = "ShowViDFrag"

        @JvmStatic
        fun newInstance(mDetailPresente: DetailPresenter, videoLocation: String) =
                ShowVideoFragment().apply {
                    arguments = Bundle().apply {
                        putString(VIDEO_LOCATION, videoLocation)
                        mDetailPresenter = mDetailPresente
                    }
                }
    }
}
