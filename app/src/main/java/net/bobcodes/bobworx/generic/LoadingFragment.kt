package net.bobcodes.bobworx.generic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import net.bobcodes.bobworx.R

class LoadingFragment : GenericFragment("LoadingFragment") {
	override fun onHiddenChanged(hidden: Boolean) {
		if (!hidden) {
			val v = view
			try {
				if (v != null) {
					v.clearFocus()
					val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					imm.hideSoftInputFromWindow(v.windowToken, 0)
				}
			} catch (e: NullPointerException) {
				//Keyboard didn't close, oh well...
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		retainInstance = true
		return inflater.inflate(R.layout.fragment_loading, container, false)
	}

	companion object {
		const val TAG = "LoadingFragment"
	}
}