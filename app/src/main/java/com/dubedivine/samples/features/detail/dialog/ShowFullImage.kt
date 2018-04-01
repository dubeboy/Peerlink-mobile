package com.dubedivine.samples.features.detail.dialog


import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide

import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.features.detail.DetailPresenter
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.ViewUtil


class ShowFullImage : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var clickedPosition: Int? = null
    private var pictureList: ArrayList<String>? = null
    private lateinit var detailPresenter: DetailPresenter
    private lateinit var context: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clickedPosition = it.getInt(CLICKED_PIC_POSITION)
            pictureList = it.getStringArrayList(PICTURES_LIST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val horizontalView = inflater.inflate(R.layout.fragment_show_full_image, container, false)
        val linearImageView: LinearLayout = horizontalView.findViewById(R.id.linearLayout_images)

        val h = horizontalView as HorizontalScrollView

        // estimated the that each image would be about 400dp so
        // then move multiply to get three of those images therefore getting the length therefor the pixels to scroll to
        h.scrollTo(clickedPosition!! * ViewUtil.dpToPx(400), 0)

        BasicUtils.addPicturesListToHorizontalListView(linearImageView, pictureList,
                activity as AppCompatActivity,
                detailPresenter,  false)

        return horizontalView

    }



    companion object {

        private const val CLICKED_PIC_POSITION = "clickedPosition"
        private const val PICTURES_LIST = "pictureList"

        @JvmStatic
        fun newInstance(detailPresenter: DetailPresenter, clickedPicturePosition: Int, picture: List<String>) =
                ShowFullImage().apply {
                    this.detailPresenter = detailPresenter
                    arguments = Bundle().apply {
                        putInt(CLICKED_PIC_POSITION, clickedPicturePosition)
                        // this ops is not that bad because we can have O(n) of 10 reducing to in the future
                        putStringArrayList(PICTURES_LIST, ArrayList(picture.map {it}))
                    }
                }
    }
}

