package com.example.face_detect_analyze_android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class FaceDetectAnalyzer(
    context: Context,
    private val listener: FaceListener,
) : ImageAnalysis.Analyzer {
    private var lbpCascadeClassifier: CascadeClassifier? = null

    init {
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

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val buffer = imageProxy.planes[0].buffer

        Log.d("asd", imageProxy.width.toString())
        Log.d("asd", imageProxy.height.toString())



        val yData = buffer.toByteArray()
        val yMat = Mat(imageProxy.height, imageProxy.width, CvType.CV_8UC1)
        yMat.put(0, 0, yData)

        val tyMat = yMat.t()

        val TAG = "asd"

        Log.d(TAG, "Mat type: ${tyMat?.type()}")
        Log.d(TAG, "Mat size: ${tyMat?.size()}")
        Log.d(TAG, "Mat channels: ${tyMat?.channels()}")
        Log.d(TAG, "Mat depth: ${tyMat?.depth()}")
        yMat.release()



        val facesRects = MatOfRect()
        lbpCascadeClassifier?.detectMultiScale(tyMat, facesRects, 1.1, 3)

        listener(facesRects.toArray(), imageProxy.height.toFloat(), imageProxy.width.toFloat())

        tyMat.release()
        facesRects.release()
        imageProxy.close()
    }


//    override fun analyze(imageProxy: ImageProxy) {
//
//        // 1. ImageProxy에서 Y 평면의 ByteBuffer를 가져와 ByteArray로 변환
//        val buffer = imageProxy.planes[0].buffer
//        val yData = buffer.toByteArray()
//
//        // 2. ByteArray를 Mat으로 변환
//        val yMat = Mat(imageProxy.height, imageProxy.width, CvType.CV_8UC1)
//        yMat.put(0, 0, yData)
//
//        val tyMat = yMat.t()
//        yMat.release()
//
//        // 3. 얼굴 인식
//        val facesRects = MatOfRect()
//        lbpCascadeClassifier?.detectMultiScale(tyMat, facesRects, 1.1, 2)
//
//        // 4. 투명한 Mat 생성. 여기서는 CV_8UC4를 사용하여 4 채널(투명도 포함)을 갖는 Mat를 만듭니다.
//        val transparentMat = Mat(tyMat.size(), CvType.CV_8UC4, Scalar(0.0, 0.0, 0.0, 0.0))
//        tyMat.release()
//
//
//        // 5. 인식된 얼굴을 그리기
//        for (rect in facesRects.toArray()) {
//            Imgproc.rectangle(transparentMat, rect, Scalar(0.0, 255.0, 0.0, 255.0), 3)
//        }
//
//        // 6. Mat을 Bitmap으로 변환
//        val resultBitmap = Bitmap.createBitmap(imageProxy.height, imageProxy.width, Bitmap.Config.ARGB_8888)
//
//        val flipMat = Mat()
//        Core.flip(transparentMat, flipMat, 1)
//        transparentMat.release()
//
//        Utils.matToBitmap(flipMat, resultBitmap)
//        flipMat.release()
//
//
//        listener(resultBitmap)
//
//        facesRects.release()
//        imageProxy.close()
//    }
}