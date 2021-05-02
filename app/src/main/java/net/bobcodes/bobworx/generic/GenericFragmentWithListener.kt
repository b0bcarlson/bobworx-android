package net.bobcodes.bobworx.generic

import android.content.Context
import android.widget.Toast

abstract class GenericFragmentWithListener(override val TAG: String) : GenericFragment(TAG) {
	protected var listener: OnFragmentInteractionListener? = null
	override fun onAttach(context: Context) {
		super.onAttach(context)
		if (context is OnFragmentInteractionListener) {
			listener = context
		} else if (parentFragment is OnFragmentInteractionListener) {
			listener = parentFragment as OnFragmentInteractionListener?
		} else if (this !is ListenerNotRequired) {
			throw RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener of " + this.javaClass.name)
		}
	}

	override fun onDetach() {
		super.onDetach()
		listener = null
	}

	protected open fun onLoading() {
		if (listener != null) listener!!.onFragmentLoading()
	}

	protected fun onError(error: Int) {
		if (listener != null) listener!!.onFragmentError(error)
	}

	protected fun onNonInterruptingError(error: Int) {
		if (listener != null) listener!!.onNonInterruptingError(this, error)
	}

	protected fun onToastError(error: Int) {
		Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
	}

	protected open fun onReady() {
		if (listener != null) listener!!.onFragmentReady(this)
	}
}