package net.bobcodes.bobworx

import android.content.Context
import android.net.Uri
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class RequestBuilder(
	val context: Context,
	val url: Int,
	val method: Int,
	val listener: Response.Listener<String>,
	val errorListener: Response.ErrorListener,
	vararg val params: Map.Entry<String, String>,
	val useDefaults: Boolean = true
) {
	fun run(){
		val queue: RequestQueue = Volley.newRequestQueue(this.context)
		val uri: Uri.Builder
		if(useDefaults) {
			uri = Utils.getUrlWithDefaults(this.context, this.url)
		}
		else{
			uri = Utils.getUrl(this.context, this.url)
		}
		this.params.forEach { e -> uri.appendQueryParameter(e.key, e.value) }

		val stringRequest = StringRequest(
			this.method, uri.build().toString(),
			listener, errorListener
		)
		stringRequest.retryPolicy = DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
		queue.add(stringRequest)
	}
	class Entry(override val key: String, override val value: String) : Map.Entry<String, String>{

	}
}