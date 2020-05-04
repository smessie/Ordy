package com.ordy.app.ui.orders.overview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityOverviewOrderBinding
import com.ordy.app.ui.orders.overview.general.OrderGeneralFragment
import com.ordy.app.ui.orders.overview.personal.OrderPersonalFragment
import com.ordy.app.ui.orders.overview.users.OrderUsersFragment
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.SnackbarUtil
import com.ordy.app.util.TabsAdapter
import com.ordy.app.util.TimerUtil
import com.ordy.app.util.types.SnackbarType
import com.ordy.app.util.types.TabsEntry
import kotlinx.android.synthetic.main.activity_overview_order.*
import kotlinx.android.synthetic.main.activity_overview_order.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DateFormat
import kotlin.properties.Delegates


class OverviewOrderActivity : AppCompatActivity() {

    private val viewModel: OverviewOrderViewModel by viewModel()

    private lateinit var tabsAdapter: TabsAdapter

    private var orderId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewOrderBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_overview_order)
        binding.handlers = OverviewOrderHandlers(this, viewModel)

        // Set the action bar elevation to 0, since the order extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        /**
         * Setup the tabsbar.
         */

        // Create the tabs adapter.
        tabsAdapter = TabsAdapter(supportFragmentManager)
        tabsAdapter.addTabsEntry(TabsEntry(OrderPersonalFragment(), "Your items"))
        tabsAdapter.addTabsEntry(TabsEntry(OrderGeneralFragment(), "Overview"))
        tabsAdapter.addTabsEntry(TabsEntry(OrderUsersFragment(), "Users"))

        // Link the adapter to the viewpager.
        val viewPager: ViewPager = binding.root.tabs_view
        viewPager.adapter = tabsAdapter

        // Link the viewpager to the tablayout.
        val tabs: TabLayout = binding.root.tabs
        tabs.setupWithViewPager(viewPager)

        // Extract the "order_id" from the given intent variables.
        orderId = intent.getIntExtra("order_id", -1)

        // Store the orderId
        viewModel.orderId.postValue(orderId)

        // Fetch the specific order.
        viewModel.orderId.observe(this, Observer {
            if (it != -1) {
                viewModel.refreshOrder()
            }
        })

        // Observe the changes of the fetch.
        viewModel.getOrderMLD().observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    val order = it.requireData()

                    order_deadline_time.text =
                        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(order.deadline)
                    order_title.text = order.location.name
                    order_location_name.text = order.location.name
                    order_courier_name.text = order.courier.username

                    // Show the bill URL when a bill is present.
                    val billUrl = it.requireData().billUrl ?: ""

                    if (!billUrl.isBlank()) {
                        order_bill_button.visibility = View.VISIBLE
                    }

                    // Update the closing time left every second.
                    viewModel.updateTimer = TimerUtil.updateUI(this, 0, 1000) {
                        order_deadline_time_left.text = OrderUtil.timeLeftFormat(order.deadline)
                    }

                    // Show the add bill FAB when the user is the courier.
                    if (it.requireData().courier.id == AppPreferences(this).userId) {
                        speeddial_orders.visibility = View.VISIBLE
                    }
                }

                QueryStatus.ERROR -> {
                    ErrorHandler().handle(it.error, binding.root)
                }

                else -> {
                }
            }
        })

        /**
         * Setup speeddial for uploading the bill picture.
         */
        val speedDialView = binding.root.speeddial_orders

        // Take bill picture
        speedDialView
            .addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.speeddial_order_camera,
                    R.drawable.ic_camera_alt_black_24dp
                )
                    .setLabel(getString(R.string.speeddial_order_camera))
                    .setFabImageTintColor(Color.WHITE)
                    .create()
            )

        // Select bill picture
        speedDialView
            .addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.speeddial_order_gallery,
                    R.drawable.ic_image_black_24dp
                )
                    .setLabel(getString(R.string.speeddial_order_gallery))
                    .setFabImageTintColor(Color.WHITE)
                    .create()
            )

        // Click actions.
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {

                // Take bill picture.
                R.id.speeddial_order_camera -> {

                    // Open camera activity
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            val imageDir = externalCacheDir

                            // Create a temporary file to store the bill image.
                            val imageFile = File.createTempFile(
                                "ordy_bill",
                                ".jpg",
                                imageDir
                            )

                            // Tell the camera to store the image at the temporary location.
                            viewModel.billUploadUri = imageFile.toURI()

                            takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(
                                    this,
                                    "$packageName.provider",
                                    imageFile
                                )
                            )

                            // Open the camera activity.
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }

                    // Close the speeddial.
                    speedDialView.close()
                }

                // Select bill picture
                R.id.speeddial_order_gallery -> {

                    // Open the gallery activity
                    Intent(Intent.ACTION_PICK).also { choosePictureIntent ->
                        choosePictureIntent.resolveActivity(packageManager)?.also {

                            // Only select images.
                            choosePictureIntent.type = "image/*"

                            // Open the gallery activity.
                            startActivityForResult(choosePictureIntent, REQUEST_IMAGE_SELECT)
                        }
                    }

                    // Close the speeddial.
                    speedDialView.close()
                }
            }
            false
        }

        // Create the alert dialog to show while the bill is uploading.
        val builder = AlertDialog.Builder(this).apply {
            setCancelable(false)
            setView(R.layout.dialog_upload_bill_loading)
        }
        val dialog = builder.create()

        // Observe changes to the bill upload.
        viewModel.getUploadBillResult().observe(this, Observer {

            when (it.status) {
                QueryStatus.LOADING -> {
                    dialog.show()
                }

                QueryStatus.SUCCESS -> {
                    dialog.hide()

                    SnackbarUtil.openSnackbar(
                        text = getString(R.string.bill_upload_success),
                        view = binding.root,
                        duration = Snackbar.LENGTH_SHORT,
                        type = SnackbarType.SUCCESS
                    )

                    // Refresh the order
                    viewModel.refreshOrder()
                }

                QueryStatus.ERROR -> {
                    dialog.hide()

                    ErrorHandler().handle(it.error, binding.root)
                }

                else -> {
                }
            }
        })
    }

    // Request code for taking a picture with the camera.
    val REQUEST_IMAGE_CAPTURE = 1

    // Request code for sekecting a picture from the gallery.
    val REQUEST_IMAGE_SELECT = 2

    /**
     * Get the result image from the camera when the picture is taken.
     * Send the picture to the server in an upload request.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // When the result is a picture from the camera.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (viewModel.billUploadUri != null) {

                val imagePath = viewModel.billUploadUri?.path

                if (imagePath != null) {
                    val imageFile = File(imagePath)

                    uploadBillFile(imageFile)
                }
            }
        }

        // When the result is a picture from the gallery.
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {

            if (data != null) {
                val image = data.data

                if (image != null) {

                    val parcelFileDescriptor = contentResolver.openFileDescriptor(image, "r")
                    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                    val imageData = BitmapFactory.decodeFileDescriptor(fileDescriptor)

                    val imageBytes = ByteArrayOutputStream()

                    // Compress the image as JPEG.
                    imageData.compress(Bitmap.CompressFormat.JPEG, 100, imageBytes)

                    uploadBillData(imageBytes.toByteArray())
                }
            }
        }
    }


    /**
     * Upload the bill to the server.
     * @param imageFile File to upload.
     */
    private fun uploadBillFile(imageFile: File) {

        // File to send in the request.
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            imageFile
        )

        // Name of the file to send in the request.
        val requestBody =
            MultipartBody.Part.createFormData("image", "bill.jpg", requestFile)

        // Upload the bill image.
        viewModel.uploadBill(orderId, requestBody)
    }

    /**
     * Upload the bill to the server.
     * @param imageBytes Array with bytes.
     */
    private fun uploadBillData(imageBytes: ByteArray) {

        // File to send in the request.
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            imageBytes
        )

        // Name of the file to send in the request.
        val requestBody =
            MultipartBody.Part.createFormData("image", "bill.jpg", requestFile)

        // Upload the bill image.
        viewModel.uploadBill(orderId, requestBody)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cancel the update timer.
        viewModel.updateTimer.cancel()
    }
}
