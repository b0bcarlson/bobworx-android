package net.bobcodes.bobworx.tracks.exactMatch

import android.annotation.SuppressLint
import android.os.Bundle
import net.bobcodes.bobworx.generic.GenericActivity
import net.bobcodes.bobworx.generic.LoadingFragment
import net.bobcodes.bobworx.generic.TextFragment

class TracksExactMatchActivity: GenericActivity(), TracksExactMatchFragment.OnFragmentInteractionListener{
	private lateinit var tracksExactMatchFragment: TracksExactMatchFragment
	@SuppressLint("MissingSuperCall")
	override fun onCreate(savedInstanceState: Bundle?) {
		val result = super.onCreate(
			savedInstanceState, LoadingFragment::class.java,
			TracksExactMatchFragment::class.java, TextFragment::class.java
		)
		getFragment(LoadingFragment.TAG)
		getFragment(TextFragment.TAG)
		tracksExactMatchFragment = getFragment(TracksExactMatchFragment.TAG) as TracksExactMatchFragment
		if(result) {
			if(intent.extras != null){
				if(intent.extras!!.containsKey("upc")) {
					tracksExactMatchFragment.upc = intent.extras!!.getString("upc")!!
					tracksExactMatchFragment.load()
				}
			}
		}
		else if(!tracksExactMatchFragment.isHidden)
			tracksExactMatchFragment.load()

	}
	override fun onBackPressed() {
		when (visibleFragment) {
			TracksExactMatchFragment.TAG, LoadingFragment.TAG, TextFragment.TAG -> {
				setResult(RESULT_OK)
				finish()
				return
			}
		}
	}
}