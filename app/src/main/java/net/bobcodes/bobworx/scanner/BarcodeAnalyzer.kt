package net.bobcodes.bobworx.scanner

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import net.bobcodes.bobworx.R

class BarcodeAnalyzer(private val barcodeListener: BarcodeListener, val context:Context) : ImageAnalysis.Analyzer {
	val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
		.setBarcodeFormats(
			Barcode.FORMAT_UPC_A
		).build()
	private val scanner = BarcodeScanning.getClient(options)

	@SuppressLint("UnsafeExperimentalUsageError")
	override fun analyze(imageProxy: ImageProxy) {
		val mediaImage = imageProxy.image
		if (mediaImage != null) {
			val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
			scanner.process(image)
				.addOnSuccessListener { barcodes ->
					for (barcode in barcodes) {
						if(barcode.rawValue != null) {
							if (barcode.rawValue!!.startsWith("2"))
								barcodeListener(barcode.rawValue!!.substring(0, 6) + "000000")
							else
								barcodeListener(barcode.rawValue!!)
						}
						break
					}
				}
				.addOnFailureListener {
					throw it
				}
				.addOnCompleteListener {
					imageProxy.close()
				}
		}
	}
}