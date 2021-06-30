package net.bobcodes.bobworx.tracks.exactMatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.android.volley.Request
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.RequestBuilder
import net.bobcodes.bobworx.generic.GenericFragmentWithListener
import org.json.JSONException
import org.json.JSONObject


class TracksExactMatchFragment : GenericFragmentWithListener(TAG) {
	lateinit var upc:String
	private lateinit var nameTV: TextView
	private lateinit var baseTV: TextView
	private lateinit var retailTV:TextView
	private lateinit var weightedCB:CheckBox
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		retainInstance = true
		val root: View =
			inflater.inflate(R.layout.fragment_tracks_exact_match, container, false)
		nameTV = root.findViewById(R.id.item_name)
		baseTV = root.findViewById(R.id.base)
		retailTV = root.findViewById(R.id.retail)
		weightedCB = root.findViewById(R.id.weighted)
		return root
	}
	fun load(){
		onLoading()
		RequestBuilder(requireContext(), R.string.url_getExactMatch, Request.Method.GET,
			{
				try {
					val obj = JSONObject(it)
					nameTV.text = obj.getString("description")
					baseTV.text = "Base: " +obj.getInt("base").toString()
					retailTV.text = "Retail: " + obj.getInt("retail").toString()
					weightedCB.isChecked = obj.getBoolean("weighted")
					onReady()
				} catch (e: JSONException) {
					e.printStackTrace()
					onError(R.string.error_compares_parse)
				}
			},
			{if(it.networkResponse.statusCode == 404)
				onError(R.string.exact_match_404)
				else
				onError(R.string.error_compares_network) },
		RequestBuilder.Entry("upc", upc)).run()
	}

	interface OnFragmentInteractionListener : net.bobcodes.bobworx.generic.OnFragmentInteractionListener {
	}
	companion object {
		const val TAG = "TracksExactMatchFragment"
	}
}