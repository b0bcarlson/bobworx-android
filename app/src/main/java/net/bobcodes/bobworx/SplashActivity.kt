package net.bobcodes.bobworx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import net.bobcodes.bobworx.login.LoginActivity
import net.bobcodes.bobworx.main.MainActivity
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class SplashActivity : AppCompatActivity() {
	private lateinit var errorTV: TextView
	private lateinit var progressBar: ProgressBar
	private lateinit var loginButton: Button
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		errorTV = findViewById(R.id.splash_error)
		progressBar = findViewById(R.id.splash_progress)
		loginButton = findViewById(R.id.splash_button_login)
		loginButton.setOnClickListener {
			startActivity(Intent(this, LoginActivity::class.java))
		}
		@Suppress("DEPRECATION") val preferences = PreferenceManager.getDefaultSharedPreferences(this)
		val uuid = preferences.getString("user_uuid", null)
		if (uuid == null) {
			loginButton.visibility = View.VISIBLE
			progressBar.visibility = View.GONE
			return
		}
		RequestBuilder(this, R.string.url_verifyToken,
			Request.Method.GET,
			{ response ->
				try {
					val result = JSONObject(response)
					if (result.getBoolean("verified")) {
						val intent = Intent(this, MainActivity::class.java)
						startActivity(intent)
						finish()
					} else {
						Utils.handleError(errorTV, progressBar, R.string.login_error_unverified)
						loginButton.visibility = View.VISIBLE
					}
				} catch (e: JSONException) {
					Utils.handleError(errorTV, progressBar, R.string.login_error_undefined)
					loginButton.visibility = View.VISIBLE
				}
			},
			{ error ->
				error.printStackTrace()
				Utils.handleError(errorTV, progressBar, R.string.login_error_undefined)
				loginButton.visibility = View.VISIBLE
			}
		).run()
	}
}