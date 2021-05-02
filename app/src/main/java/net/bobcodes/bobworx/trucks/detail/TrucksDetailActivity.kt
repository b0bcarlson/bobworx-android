package net.bobcodes.bobworx.trucks.detail

import android.annotation.SuppressLint
import android.os.Bundle
import net.bobcodes.bobworx.generic.GenericActivity
import net.bobcodes.bobworx.generic.LoadingFragment
import net.bobcodes.bobworx.generic.TextFragment
import net.bobcodes.bobworx.scanner.ScannerFragment

class TrucksDetailActivity: GenericActivity(), TrucksDetailFragment.OnFragmentInteractionListener,
ScannerFragment.OnFragmentInteractionListener{
	lateinit var trucksDetailFragment: TrucksDetailFragment
	lateinit var scannerFragment: ScannerFragment
	@SuppressLint("MissingSuperCall")
	override fun onCreate(savedInstanceState: Bundle?) {
		val result = super.onCreate(
			savedInstanceState, LoadingFragment::class.java,
			TrucksDetailFragment::class.java, TextFragment::class.java,
			ScannerFragment::class.java
		)
		getFragment(LoadingFragment.TAG)
		getFragment(TextFragment.TAG)
		trucksDetailFragment = getFragment(TrucksDetailFragment.TAG) as TrucksDetailFragment
		scannerFragment = getFragment(ScannerFragment.TAG) as ScannerFragment
		if(result) {
			if(intent.extras != null){
				if(intent.extras!!.containsKey("item"))
					trucksDetailFragment.setItem(intent.extras!!.getString("item")!!)
			}
		}
		else if(!trucksDetailFragment.isHidden)
			trucksDetailFragment.load()

	}
	override fun onBackPressed() {
		when (visibleFragment) {
			TrucksDetailFragment.TAG, LoadingFragment.TAG, TextFragment.TAG -> {
				setResult(RESULT_OK)
				finish()
				return
			}
			scannerFragment.TAG -> trucksDetailFragment.load()
		}
	}

	override fun onResume() {
		if(visibleFragment.equals(scannerFragment.TAG))
			trucksDetailFragment.load()
		super.onResume()
	}

	override fun onFabClicked() {
		switchFragment(scannerFragment)
	}

	override fun onResetScanner() {
		scannerFragment.resetScanner()
	}

	override fun onBarcodeRead(id: String) {
		trucksDetailFragment.addUpc(id)
	}
}