package com.example.scannertest.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.manateeworks.BarcodeScanner
import com.manateeworks.BarcodeScanner.MWB_CODE_MASK_QR

import com.manateeworks.BarcodeScanner.MWB_CODE_MASK_PDF

import com.manateeworks.BarcodeScanner.MWB_CODE_MASK_DM

import java.nio.ByteBuffer


suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            executor
        )
    }
}

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

suspend fun ImageCapture.takePicture(executor: Executor): File {
    val photoFile = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            File.createTempFile("image", "jpg")
        }.getOrElse { ex ->
            Log.e("TakePicture", "Failed to create temporary file", ex)
            File("/dev/null")
        }
    }

    return suspendCoroutine { continuation ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        takePicture(
            outputOptions, executor,
            object :  ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    //val rect = output.

                    continuation.resume(photoFile)
                }

                override fun onError(ex: ImageCaptureException) {
                    Log.e("TakePicture", "Image capture failed", ex)
                    continuation.resumeWithException(ex)
                }
            }
        )
    }
}

//ImageCapture.OnImageCapturedCallback{
//                onCaptureSuccess(image: ImageProxy){
//                     BarcodeProcess(image.getPlanes()[0].getBuffer(), image.getWidth(), image.getHeight())
//                }




fun BarcodeProcess(image: ByteBuffer, width: Int, height: Int){

    // register your copy of the mobiScan SDK with the given user name / key
    /*
        SG.Android.PDF.4DL	67145DFE6112EF527ECB15A8E8DBFCB957B3E1E4C8BC78E50D653DA991D35969
		SG.Android.QR.UDL	F4BE0ADD1DF16AD05C42731EA07C7E0476C48191F68F70409CF06B2E6D8022C5
		SG.Android.C39.UDL	79E1652140F159596D59471E445DE468E7A529505E1D77B2F97ECBF1AF721B40
		SG.Android.C93.UDL	FA515BE6703993DAC6A65BE8A73EFFC049D9CA2BAF36CA7F734FE76E55640851
		SG.Android.DM.UDL	2247C70E0A9F665FA919F1912559138E8AE94CE544E12F67BCEE4B8E42B0E9E8
		SG.Android.EANUPC.UDL	8F0D7233DF33E96526149264414040C0FC63A6BC7271BB702862E2F7F0464CD1
		SG.Android.C128.UDL	D439BE28534FDFB16913D0C584E9C4550D41232E795F0CAC03ED3ADB6FA07E42
		SG.Android.C25.UDL	F0790953014F4CB8D0F8EEA211D03589AE8F0F9CBAADEBC7E13579B0A4AE882E
		SG.Android.CB.UDL	FF8CFAFD6271BF7D2F4788578EDB9180C165ED40810157A2A8235734001509D4
        */BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_PDF,
        "SG.Android.PDF.4DL",
        "67145DFE6112EF527ECB15A8E8DBFCB957B3E1E4C8BC78E50D653DA991D35969"
    )
    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_QR,
        "SG.Android.QR.UDL",
        "F4BE0ADD1DF16AD05C42731EA07C7E0476C48191F68F70409CF06B2E6D8022C5"
    )

    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_39,
        "SG.Android.C39.UDL",
        "79E1652140F159596D59471E445DE468E7A529505E1D77B2F97ECBF1AF721B40"
    )
    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_93,
        "SG.Android.C93.UDL",
        "FA515BE6703993DAC6A65BE8A73EFFC049D9CA2BAF36CA7F734FE76E55640851"
    )

    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_DM,
        "SG.Android.DM.UDL",
        "2247C70E0A9F665FA919F1912559138E8AE94CE544E12F67BCEE4B8E42B0E9E8"
    )

    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_EANUPC,
        "SG.Android.EANUPC.UDL",
        "8F0D7233DF33E96526149264414040C0FC63A6BC7271BB702862E2F7F0464CD1"
    )

    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_128,
        "SG.Android.C128.UDL",
        "D439BE28534FDFB16913D0C584E9C4550D41232E795F0CAC03ED3ADB6FA07E42"
    )
    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_25,
        "SG.Android.C25.UDL",
        "F0790953014F4CB8D0F8EEA211D03589AE8F0F9CBAADEBC7E13579B0A4AE882E"
    )
    BarcodeScanner.MWBregisterCode(
        BarcodeScanner.MWB_CODE_MASK_CODABAR,
        "SG.Android.CB.UDL",
        "FF8CFAFD6271BF7D2F4788578EDB9180C165ED40810157A2A8235734001509D4"
    )
    val status: Int = BarcodeScanner.MWBsetActiveCodes(
        MWB_CODE_MASK_DM or
                MWB_CODE_MASK_PDF or
                MWB_CODE_MASK_QR
    )
    var rawResult: ByteArray? = null
    rawResult = BarcodeScanner.MWBscanGrayscaleImage(image.array(), width, height)
    if (rawResult.size > 0) {
        // Process result
    }
}