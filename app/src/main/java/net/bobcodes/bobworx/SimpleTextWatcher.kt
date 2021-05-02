package net.bobcodes.bobworx

import android.text.Editable
import android.text.TextWatcher

abstract class SimpleTextWatcher : TextWatcher {
	override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
	override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
	abstract override fun afterTextChanged(editable: Editable)
}