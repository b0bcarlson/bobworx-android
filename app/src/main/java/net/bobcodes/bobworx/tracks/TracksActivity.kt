package net.bobcodes.bobworx.tracks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.Utils
import net.bobcodes.bobworx.generic.GenericActivity
import net.bobcodes.bobworx.generic.LoadingFragment
import net.bobcodes.bobworx.generic.TextFragment
import net.bobcodes.bobworx.model.Item
import net.bobcodes.bobworx.scanner.ScannerFragment
import net.bobcodes.bobworx.tracks.detail.TracksDetailActivity
import net.bobcodes.bobworx.tracks.exactMatch.TracksExactMatchActivity
import org.json.JSONException

class TracksActivity: GenericActivity(), TracksFragment.OnFragmentInteractionListener, ScannerFragment.OnFragmentInteractionListener {
	private lateinit var tracksFragment: TracksFragment
	private lateinit var scannerFragment: ScannerFragment
	private lateinit var upcs: HashMap<String, String>
	private var activityStarted = false
	@SuppressLint("MissingSuperCall")
	override fun onCreate(savedInstanceState: Bundle?) {
		val result = super.onCreate(
			savedInstanceState, LoadingFragment::class.java,
			TracksFragment::class.java, TextFragment::class.java, ScannerFragment::class.java
		)
		getFragment(LoadingFragment.TAG)
		getFragment(TextFragment.TAG)
		scannerFragment = getFragment(ScannerFragment.TAG) as ScannerFragment
		tracksFragment = getFragment(TracksFragment.TAG) as TracksFragment
		if(result)
			tracksFragment.getCompares()
		else if(!tracksFragment.isHidden)
			tracksFragment.getCompares()

	}
	override fun onBackPressed() {
		when (visibleFragment) {
			TracksFragment.TAG, LoadingFragment.TAG, TextFragment.TAG -> {
				finish()
				return
			}
			ScannerFragment.TAG -> switchFragment(tracksFragment)
		}
	}
	override fun onOpenScanner() {
		switchFragment(scannerFragment)
	}

	override fun onItemSelected(item: Item) {
		val intent = Intent(
			this,
			TracksDetailActivity::class.java
		)
		val bundle = Bundle()
		try {
			bundle.putString("item", item.id)
		} catch (e: JSONException) {
			e.printStackTrace()
		}
		intent.putExtras(bundle)
		startActivity(intent)
	}

	override fun onUpcs(upcs: HashMap<String, String>) {
		this.upcs = upcs
	}
	private fun onResetScanner(resetRead: Boolean = false){
		scannerFragment.resetScanner(resetRead)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		activityStarted = requestCode != 999
		onResetScanner(true)
	}

	override fun onBarcodeRead(upc: String) {
		if(activityStarted)
			return
		if (upcs.containsKey(upc))
			try {
				val intent = Intent(
					this,
					TracksDetailActivity::class.java
				)
				val bundle = Bundle()
				bundle.putString("item", upcs[upc])
				intent.putExtras(bundle)
				startActivityForResult(intent, 999)
				activityStarted = true
			} catch (e: JSONException) {
				e.printStackTrace()
			}
		else{
			Utils.playsound(this, R.raw.error2)
			if(!Utils.isUpcPrivateLabel(upc)){
					AlertDialog.Builder(this).setMessage("$upc is not tracked in TRACKS, would you like to search for an exact match?")
					.setPositiveButton(R.string.ok
					) { _, _ ->
						val intent = Intent(
							this,
							TracksExactMatchActivity::class.java
						)
						val bundle = Bundle()
						bundle.putString("upc", upc)
						intent.putExtras(bundle)
						startActivityForResult(intent, 999)
						activityStarted = true
					}
						.setNegativeButton(R.string.cancel
					) { dialog, _ ->
						dialog.dismiss()
					}.create().show()
			}
		}
		onResetScanner()
	}


}