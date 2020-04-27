package com.ordy.app.ui.orders.overview.viewbill

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.github.chrisbanes.photoview.PhotoView
import com.ordy.app.R
import com.ordy.app.api.ApiServiceProvider
import com.ordy.app.databinding.DialogViewBillBinding
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_view_bill.view.*
import java.lang.Exception

class ViewBillDialog : DialogFragment() {

    private val viewModel: OverviewOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        inflater.inflate(R.layout.dialog_view_bill, container, false)

        // Create binding for the fragment.
        val binding = DialogViewBillBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        // Setup the toolbar
        val toolbar: Toolbar = binding.toolbar
        toolbar.title = "View bill: ${viewModel.getOrder().requireData().location.name}"
        toolbar.setNavigationOnClickListener { dismiss() }

        val photoView = binding.root.findViewById(R.id.bill_image) as PhotoView

        Picasso.Builder(requireContext())
            .downloader(OkHttp3Downloader(ApiServiceProvider().client(requireContext())))
            .build()
            .load(viewModel.getOrder().requireData().billUrl)
            .into(photoView, object : Callback {

                override fun onSuccess() {
                    binding.root.bill_image_loading.visibility = View.INVISIBLE
                }

                override fun onError(e: Exception?) {
                    binding.root.bill_image_loading.visibility = View.INVISIBLE
                    binding.root.bill_image_error.visibility = View.VISIBLE
                }
            })



        return binding.root
    }
}