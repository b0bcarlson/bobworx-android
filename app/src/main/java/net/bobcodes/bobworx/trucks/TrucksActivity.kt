package net.bobcodes.bobworx.trucks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.Utils
import net.bobcodes.bobworx.generic.GenericActivity
import net.bobcodes.bobworx.generic.LoadingFragment
import net.bobcodes.bobworx.generic.TextFragment
import net.bobcodes.bobworx.model.Item
import net.bobcodes.bobworx.scanner.ScannerFragment
import net.bobcodes.bobworx.trucks.detail.TrucksDetailActivity
import org.json.JSONException

class TrucksActivity: GenericActivity(), TrucksFragment.OnFragmentInteractionListener, ScannerFragment.OnFragmentInteractionListener {
	lateinit var trucksFragment: TrucksFragment
	lateinit var scannerFragment: ScannerFragment
	lateinit var upcs: HashMap<String, String>
	var activityStarted = false
	@SuppressLint("MissingSuperCall")
	override fun onCreate(savedInstanceState: Bundle?) {
		val result = super.onCreate(
			savedInstanceState, LoadingFragment::class.java,
			TrucksFragment::class.java, TextFragment::class.java, ScannerFragment::class.java
		)
		getFragment(LoadingFragment.TAG)
		getFragment(TextFragment.TAG)
		scannerFragment = getFragment(ScannerFragment.TAG) as ScannerFragment
		trucksFragment = getFragment(TrucksFragment.TAG) as TrucksFragment
		if(result)
			trucksFragment.getCompares()
		else if(!trucksFragment.isHidden)
			trucksFragment.getCompares()

	}
	override fun onBackPressed() {
		when (visibleFragment) {
			TrucksFragment.TAG, LoadingFragment.TAG, TextFragment.TAG -> {
				finish()
				return
			}
			ScannerFragment.TAG -> switchFragment(trucksFragment)
		}
	}
	override fun onOpenScanner() {
		switchFragment(scannerFragment)
	}

	override fun onItemSelected(item: Item) {
		val intent = Intent(
			this,
			TrucksDetailActivity::class.java
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
	fun onResetScanner(resetRead: Boolean = false){
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
		val intent = Intent(
			this,
			TrucksDetailActivity::class.java
		)
		val bundle = Bundle()
		if (upcs.containsKey(upc))
			try {
				bundle.putString("item", upcs[upc])
				intent.putExtras(bundle)
				startActivityForResult(intent, 999)
				activityStarted = true
			} catch (e: JSONException) {
				e.printStackTrace()
			}
		else
			Utils.playsound(this, R.raw.error2)
		onResetScanner()
	}


}