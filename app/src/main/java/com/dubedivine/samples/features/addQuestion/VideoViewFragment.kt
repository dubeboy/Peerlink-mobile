package com.dubedivine.samples.features.addQuestion

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.VideoView
import com.dubedivine.samples.R
import com.dubedivine.samples.features.base.BaseBottomSheetFragment
import com.dubedivine.samples.util.snack

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [VideoViewFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [VideoViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoViewFragment : BaseBottomSheetFragment() {

    override val layout: Int
        get() = R.layout.fragment_video_view

    // TODO: Rename and change types of parameters
    private var videoURI: String? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val videoView = view!!.findViewById<VideoView>(R.id.fragment_vid)
        videoURI = arguments.getString(VIDEO_URI, "")
        if (videoURI != "") {
            videoView.setVideoPath(videoURI)
            videoView.start()
        } else {
            snack("Sorry something went wrong while trying to open video")
        }
    }

    companion object {
        private val VIDEO_URI = "videoURI"

        fun newInstance(videoUri: String): VideoViewFragment {
            val fragment = VideoViewFragment()
            val args = Bundle()
            args.putString(VIDEO_URI, videoUri)
            fragment.arguments = args
            return fragment
        }
    }
}
