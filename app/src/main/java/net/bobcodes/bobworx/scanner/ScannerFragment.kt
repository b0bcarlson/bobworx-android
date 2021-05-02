package net.bobcodes.bobworx.scanner

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_scanner.*
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.Utils
import net.bobcodes.bobworx.generic.GenericFragmentWithListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

class ScannerFragment : GenericFragmentWithListener(TAG) {
	private lateinit var cameraExecutor: ExecutorService
	private var gotUPC: Boolean = false
	private var readUpcs = ArrayList<String>()
	override fun onHiddenChanged(hidden: Boolean) {
		if(!hidden){
			cameraExecutor = Executors.newSingleThreadExecutor()
			if (allPermissionsGranted()) {
				startCamera()
			} else {
				requestPermissions(
					REQUIRED_PERMISSIONS,
					REQUEST_CODE_PERMISSIONS
				)
			}
		}
		else{
			try {
				cameraExecutor.shutdown()
			}catch (e: Exception){}
		}
		super.onHiddenChanged(hidden)
	}
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v = inflater.inflate(R.layout.fragment_scanner, container, false)
		return v
	}

	override fun onResume() {
		super.onResume()
		gotUPC = false
	}

	private fun startCamera() {
		val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
		cameraProviderFuture.addListener({
			val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
			val preview = Preview.Builder().build().also {
				it.setSurfaceProvider(
					fragment_scan_barcode_preview_view.surfaceProvider
				)
			}
			val imageAnalysis = ImageAnalysis.Builder()
				.setTargetResolution(Size(1920, 1080))
				.build()
				.also {
					it.setAnalyzer(cameraExecutor, BarcodeAnalyzer ({ barcode ->
						Log.d("Barcode", barcode)
						if(!gotUPC) {
							gotUPC = true
							if(barcode !in readUpcs) {
								readUpcs.add(barcode)
								Utils.playsound(requireContext(), R.raw.beep)
								onBarcodeRead("0" + barcode.dropLast(1) + "0")
							}
							else{
								Log.d("Barcode", "Already read, ignoring")
								resetScanner()
							}
						}
						else
							Log.d("Barcode", "Already processing UPC, ignoring")
						},
					 requireContext()))
				}
			val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
			try {
				cameraProvider.unbindAll()
				cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
			} catch (e: Exception) {
				Log.e("PreviewUseCase", "Binding failed! :(", e)
			}
		}, ContextCompat.getMainExecutor(requireContext()))
	}

	private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
		ContextCompat.checkSelfPermission(
			requireContext(), it
		) == PackageManager.PERMISSION_GRANTED
	}
	fun onBarcodeRead(upc: String){
		(listener as OnFragmentInteractionListener).onBarcodeRead(upc)
	}
	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<String>, grantResults:
		IntArray
	) {
		if (requestCode == REQUEST_CODE_PERMISSIONS) {
			if (allPermissionsGranted()) {
				startCamera()
			} else {
				Toast.makeText(
					requireContext(),
					"Permissions not granted by the user.",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}
	fun resetScanner(resetRead: Boolean = false){
		Log.d("Barcode", "Resetting scanner")
		gotUPC = false
		if(resetRead)
			readUpcs.clear()
	}
	interface OnFragmentInteractionListener : net.bobcodes.bobworx.generic.OnFragmentInteractionListener {
		fun onBarcodeRead(upc: String)
	}

	companion object {
		private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)
		private const val REQUEST_CODE_PERMISSIONS = 10
		const val TAG = "ScannerFragment"
	}
}