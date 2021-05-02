package net.bobcodes.bobworx.generic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.bobcodes.bobworx.R

class TextFragment : GenericFragmentWithListener(TAG) {
	private lateinit var textView: TextView
	private var text = ""
	private lateinit var fab: FloatingActionButton
	private var fabListener: OnFloatingActionButtonListener? = null
	fun setText(text: Int) {
		this.text = getString(text)
		if (textView != null) {
			textView.text = getString(text)
			onReady()
		}
	}

	fun showFab() {
		fabListener = if (requireContext() is OnFloatingActionButtonListener) {
			requireContext() as OnFloatingActionButtonListener
		} else if (parentFragment is OnFloatingActionButtonListener) {
			parentFragment as OnFloatingActionButtonListener?
		} else {
			throw RuntimeException(requireContext().toString()
					+ " must implement OnFloatingActionButtonListener")
		}
		fab.show()
	}

	fun hideFab() {
		fab.hide()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		retainInstance = true
		val view = inflater.inflate(R.layout.fragment_text, container, false)
		textView = view.findViewById(R.id.text_fragment_textview)
		if (text != "") textView.text = text
		fab = view.findViewById(R.id.fab_add)
		fab.setOnClickListener(View.OnClickListener { fabListener!!.onTextFragmentFabClicked() })
		return view
	}

	interface OnFloatingActionButtonListener {
		fun onTextFragmentFabClicked()
	}

	companion object {
		const val TAG = "TextFragment"
	}
}