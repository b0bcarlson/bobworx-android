package net.bobcodes.bobworx.generic

interface OnFragmentInteractionListener {
	fun onFragmentLoading()
	fun onFragmentReady(fragment: GenericFragment)
	fun onFragmentError(error: Int)
	fun onNonInterruptingError(fragment: GenericFragment, error: Int)
}