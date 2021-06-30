package net.bobcodes.bobworx

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

object Utils {
	fun handleError(errorView: TextView, progressBar: ProgressBar, errorString: Int) {
		errorView.setText(errorString)
		progressBar.visibility = View.GONE
		errorView.visibility = View.VISIBLE
	}

	fun handleError(errorView: TextView, progressBar: ProgressBar, errorString: String?) {
		errorView.text = errorString
		progressBar.visibility = View.GONE
		errorView.visibility = View.VISIBLE
	}

	private fun getToken(context: Context?): String? {
		@Suppress("DEPRECATION") val preferences = PreferenceManager.getDefaultSharedPreferences(context)
		return preferences.getString("user_token", null)
	}

	fun getUUID(context: Context?): String? {
		@Suppress("DEPRECATION") val preferences = PreferenceManager.getDefaultSharedPreferences(context)
		return preferences.getString("user_uuid", null)
	}

	@SuppressLint("HardwareIds")
	fun getDeviceID(context: Context): String {
		return Settings.Secure.getString(context.contentResolver,
				Settings.Secure.ANDROID_ID)
	}

	fun getUrl(context: Context, url: Int?): Uri.Builder {
		return Uri.parse(context.getString(R.string.url_base) + context.getString(url!!)).buildUpon()
	}

	fun getUrlWithDefaults(context: Context, url: Int?): Uri.Builder {
		val builder = Uri.parse(context.getString(R.string.url_base) +  context.getString(url!!)).buildUpon()
		builder.appendQueryParameter("id", getUUID(context))
		builder.appendQueryParameter("token", getToken(context))
		builder.appendQueryParameter("device_id", getDeviceID(context))
		builder.appendQueryParameter("method", "app")
		return builder
	}
	fun playsound(context: Context, sound: Int){
		val mp = MediaPlayer.create(context, sound);
		mp.setVolume(.03f, .03f)
		mp.setOnCompletionListener { mp.release() }
		mp.start()
	}
	fun isUpcPrivateLabel(upc:String): Boolean{
		//When Bobworx2 is released this will be changed to an api call
		return (upc.startsWith("075450")||
				upc.startsWith("036800")||
				upc.startsWith("011225")||
				upc.startsWith("2"))
	}
}