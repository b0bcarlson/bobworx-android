package net.bobcodes.bobworx.tracks.detail

import android.annotation.SuppressLint
import android.os.Bundle
import net.bobcodes.bobworx.generic.GenericActivity
import net.bobcodes.bobworx.generic.LoadingFragment
import net.bobcodes.bobworx.generic.TextFragment
import net.bobcodes.bobworx.scanner.ScannerFragment

class TracksDetailActivity: GenericActivity(), TracksDetailFragment.OnFragmentInteractionListener,
ScannerFragment.OnFragmentInteractionListener{
	lateinit var tracksDetailFragment: TracksDetailFragment
	lateinit var scannerFragment: ScannerFragment
	@SuppressLint("MissingSuperCall")
	override fun onCreate(savedInstanceState: Bundle?) {
		val result = super.onCreate(
			savedInstanceState, LoadingFragment::class.java,
			TracksDetailFragment::class.java, TextFragment::class.java,
			ScannerFragment::class.java
		)
		getFragment(LoadingFragment.TAG)
		getFragment(TextFragment.TAG)
		tracksDetailFragment = getFragment(TracksDetailFragment.TAG) as TracksDetailFragment
		scannerFragment = getFragment(ScannerFragment.TAG) as ScannerFragment
		if(result) {
			if(intent.extras != null){
				if(intent.extras!!.containsKey("item"))
					tracksDetailFragment.setItem(intent.extras!!.getString("item")!!)
			}
		}
		else if(!tracksDetailFragment.isHidden)
			tracksDetailFragment.load()

	}
	override fun onBackPressed() {
		when (visibleFragment) {
			TracksDetailFragment.TAG, LoadingFragment.TAG, TextFragment.TAG -> {
				setResult(RESULT_OK)
				finish()
				return
			}
			scannerFragment.TAG -> tracksDetailFragment.load()
		}
	}

	override fun onResume() {
		if(visibleFragment.equals(scannerFragment.TAG))
			tracksDetailFragment.load()
		super.onResume()
	}

	override fun onFabClicked() {
		switchFragment(scannerFragment)
	}

	override fun onResetScanner() {
		scannerFragment.resetScanner()
	}

	override fun onBarcodeRead(upc: String) {
		tracksDetailFragment.addUpc(upc)
	}
}