package net.bobcodes.bobworx.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.generic.GenericActivity
import net.bobcodes.bobworx.generic.LoadingFragment
import net.bobcodes.bobworx.main.MainActivity

class LoginActivity : GenericActivity(), LoginFragment.OnFragmentInteractionListener {
	@SuppressLint("MissingSuperCall")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState, LoginFragment::class.java, LoadingFragment::class.java)
		getFragment(LoginFragment.TAG)
		getFragment(LoadingFragment.TAG)
	}
	@SuppressLint("ApplySharedPref")
	override fun onLoginSuccess(id: String?, token: String?) {
		@Suppress("DEPRECATION") val preferences = PreferenceManager.getDefaultSharedPreferences(this)
		val editor = preferences.edit()
		editor.putString("user_uuid", id)
		editor.putString("user_token", token)
		editor.commit()
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
		finishAffinity()
	}
}