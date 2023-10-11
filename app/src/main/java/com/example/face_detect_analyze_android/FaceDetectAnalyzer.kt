package com.example.face_detect_analyze_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.CameraActivity
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class FaceDetectAnalyzer(
    context: Context,
    private val listener: FaceListener
) : ImageAnalysis.Analyzer {
    private var lbpCascadeClassifier: CascadeClassifier? = null


    init {
        //s dsd
        if (OpenCVLoader.initDebug()) {

            val inputStream =  context.resources.openRawResource(org.opencv.R.raw.lbpcascade_frontalface)
            val file = File(context.getDir(
                "cascade", Context.MODE_PRIVATE),
                "lbpcascade_frontalface.xml")
            val fileOutputStream = FileOutputStream(file)
            // asd
            val data = ByteArray(4096)
            var readBytes: Int

            while (inputStream.read(data).also { readBytes = it } != -1) {
                fileOutputStream.write(data, 0, readBytes)
            }

            lbpCascadeClassifier = CascadeClassifier(file.absolutePath)


            inputStream.close()
            fileOutputStream.close()
            file.delete()

        }
    }


    override fun analyze(image: ImageProxy) {
        val bitmap: Bitmap = image.toBitmap()
        val mat = Mat()
        val rects = MatOfRect()

        Utils.bitmapToMat(bitmap, mat)

        lbpCascadeClassifier?.detectMultiScale(
            mat, rects, 1.1, 2)

        val transparentMat = Mat.zeros(mat.size(), CvType.CV_8UC4)

        for (rect in rects.toList()) {
            Imgproc.rectangle(transparentMat, rect, Scalar(0.0, 255.0, 0.0), 3)
        }

        val resultBitmap = Bitmap.createBitmap(transparentMat.cols(), transparentMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(transparentMat, resultBitmap)

        listener(resultBitmap)

        image.close()
        mat.release()
        rects.release()
        transparentMat.release()
    }
}