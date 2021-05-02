package net.bobcodes.bobworx.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.RequestBuilder
import net.bobcodes.bobworx.SimpleTextWatcher
import net.bobcodes.bobworx.Utils
import net.bobcodes.bobworx.generic.GenericFragmentWithListener
import org.json.JSONException
import org.json.JSONObject
import java.util.Map

class LoginFragment : GenericFragmentWithListener(TAG) {
	private lateinit var usernameET: EditText
	private lateinit var passwordET: EditText
	private fun login() {
		onLoading()
		RequestBuilder(requireContext(), R.string.url_getToken,Request.Method.GET,
			{ response ->
				try {
					val result = JSONObject(response)
					if (result.has("error")) onToastError(R.string.error_login) else onSuccess(result.getString("id"), result.getString("app_token"))
				} catch (e: JSONException) {
					onToastError(R.string.login_error_undefined)
				}
			},
			{
				onToastError(R.string.login_error_undefined)
			},
			RequestBuilder.Entry("username", usernameET.text.toString()),
			RequestBuilder.Entry("password", passwordET.text.toString()),
			RequestBuilder.Entry("device_id", Utils.getDeviceID(requireContext())),
			useDefaults = false
		).run()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_login, container, false)
		usernameET = root.findViewById(R.id.login_username_et)
		passwordET = root.findViewById(R.id.login_password_et)
		val button = root.findViewById<Button>(R.id.login_login_bt)
		button.setOnClickListener { login() }
		usernameET.requestFocus()
		val textWatcher: TextWatcher = object : SimpleTextWatcher() {
			override fun afterTextChanged(editable: Editable) {
				button.isEnabled = usernameET.text.toString().isNotEmpty() && passwordET.text.toString().length >= 4
			}
		}
		usernameET.addTextChangedListener(textWatcher)
		passwordET.addTextChangedListener(textWatcher)
		return root
	}

	private fun onSuccess(id: String, token: String) {
		if (listener != null) (listener as OnFragmentInteractionListener).onLoginSuccess(id, token)
	}

	interface OnFragmentInteractionListener : net.bobcodes.bobworx.generic.OnFragmentInteractionListener {
		fun onLoginSuccess(id: String?, token: String?)
	}

	companion object {
		const val TAG = "LoginFragment"
	}
}