package com.ssspvtltd.quick.ui.fairOrder

import android.Manifest
import android.content.ContentValues
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityCustomerDetailsBinding
import com.ssspvtltd.quick.databinding.ActivityFairOrderBinding
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class FairOrderActivity  : BaseActivity<ActivityFairOrderBinding, BaseViewModel>()  {
    private lateinit var previewView: PreviewView
    private lateinit var imageView: ImageView
    override val inflate: InflateA<ActivityFairOrderBinding>
        get() = ActivityFairOrderBinding ::inflate

    override fun initViewModel(): BaseViewModel  = getViewModel()


    private var imageCapture: ImageCapture? = null

    // Store all captured images
    private val bitmapList = mutableListOf<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewView = binding.previewView
        imageView = binding.imageView
        // Request Camera Permission
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        // Capture Button
       binding.btnCapture.setOnClickListener {
            capturePhoto()
        }

        // Merge Button
        binding.btnDone.setOnClickListener {

            if (bitmapList.isNotEmpty()) {

                // Merge all images
                val mergedBitmap =
                    mergeBitmapsVertically(bitmapList)

                // Show merged image
                imageView.setImageBitmap(mergedBitmap)

                // Save merged image
                saveBitmap(mergedBitmap)

                // Create PDF
                createPdf(bitmapList)
            }
        }
    }

    // Permission Launcher
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                startCamera()
            }
        }

    // Start Camera
    private fun startCamera() {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            preview.setSurfaceProvider(
                previewView.surfaceProvider
            )

            imageCapture =
                ImageCapture.Builder().build()

            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    // Capture Image
    private fun capturePhoto() {

        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues().apply {

            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "IMG_${System.currentTimeMillis()}"
            )

            put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/jpeg"
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/ScannerApp"
                )
            }
        }














        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),

            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {

                    val savedUri = outputFileResults.savedUri

                    savedUri?.let {

                        val bitmap = uriToBitmap(it)

                        bitmap?.let { bmp ->

                            // Resize image
                            val resizedBitmap =
                                resizeBitmap(bmp)

                            // Check blur
                      /*      if (isImageBlurry(resizedBitmap)) {

                                Toast.makeText(
                                    this@FairOrderActivity,
                                    "Blur Image Please Capture Again",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }*/

                            // Success Alert
                            Toast.makeText(
                                this@FairOrderActivity,
                                "Image Captured Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Add image in list
                            bitmapList.add(resizedBitmap)
                        }
                    }
                }

                override fun onError(
                    exception: ImageCaptureException
                ) {

                    exception.printStackTrace()
                }
            }
        )
    }

    // Uri To Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {

        return try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                val source = ImageDecoder.createSource(
                    contentResolver,
                    uri
                )

                val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->

                    decoder.allocator =
                        ImageDecoder.ALLOCATOR_SOFTWARE

                    decoder.isMutableRequired = true
                }

                bitmap.copy(Bitmap.Config.ARGB_8888, true)

            } else {

                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    uri
                )

                bitmap.copy(Bitmap.Config.ARGB_8888, true)
            }

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    // Resize Bitmap
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            800,
            1000,
            true
        )

        return resizedBitmap.copy(
            Bitmap.Config.ARGB_8888,
            true
        )
    }

    // Merge Images Vertically
    private fun mergeBitmapsVertically(
        bitmaps: List<Bitmap>
    ): Bitmap {

        val width = bitmaps.maxOf { it.width }

        val height = bitmaps.sumOf { it.height }

        val mergedBitmap =
            Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )

        val canvas = Canvas(mergedBitmap)

        var currentHeight = 0

        bitmaps.forEach {

            canvas.drawBitmap(
                it,
                0f,
                currentHeight.toFloat(),
                null
            )

            currentHeight += it.height
        }

        return mergedBitmap
    }

    // Save Final Merged Image
    private fun saveBitmap(bitmap: Bitmap) {

        val file = File(
            getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ),
            "merged_image.jpg"
        )

        val outputStream = FileOutputStream(file)

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            90,
            outputStream
        )

        outputStream.flush()
        outputStream.close()
    }

    // Create PDF
    private fun createPdf(
        bitmaps: List<Bitmap>
    ) {

        val document = PdfDocument()

        bitmaps.forEachIndexed { index, bitmap ->

            val pageInfo =
                PdfDocument.PageInfo.Builder(
                    bitmap.width,
                    bitmap.height,
                    index + 1
                ).create()

            val page = document.startPage(pageInfo)

            page.canvas.drawBitmap(
                bitmap,
                0f,
                0f,
                null
            )

            document.finishPage(page)
        }

        val file = File(
            getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS
            ),
            "scan_document.pdf"
        )

        document.writeTo(
            FileOutputStream(file)
        )

        document.close()
    }
}

/*
private fun isImageBlurry(bitmap: Bitmap): Boolean {

    val mat = Mat()

    Utils.bitmapToMat(bitmap, mat)

    val grayMat = Mat()

    Imgproc.cvtColor(
        mat,
        grayMat,
        Imgproc.COLOR_BGR2GRAY
    )

    val laplacianMat = Mat()

    Imgproc.Laplacian(
        grayMat,
        laplacianMat,
        CvType.CV_64F
    )

    val mean = MatOfDouble()
    val stddev = MatOfDouble()

    Core.meanStdDev(
        laplacianMat,
        mean,
        stddev
    )

    val variance =
        stddev.get(0, 0)[0] *
                stddev.get(0, 0)[0]

    Log.d("BlurCheck", "Variance: $variance")

    return variance < 100
}*/
