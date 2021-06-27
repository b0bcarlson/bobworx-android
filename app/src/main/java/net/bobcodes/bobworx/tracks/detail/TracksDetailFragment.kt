package net.bobcodes.bobworx.tracks.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.RequestBuilder
import net.bobcodes.bobworx.Utils
import net.bobcodes.bobworx.generic.GenericFragmentWithListener
import net.bobcodes.bobworx.model.Item
import net.bobcodes.bobworx.model.Model
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.nio.charset.Charset


class TracksDetailFragment : GenericFragmentWithListener(TAG) {
	lateinit var itemId:String
	lateinit var itemUpc:String
	lateinit var nameTV: TextView
	lateinit var compTV: TextView
	lateinit var contTV:TextView
	lateinit var diffTV:TextView
	lateinit var tripped:CheckBox
	lateinit var signed:CheckBox
	lateinit var fab:FloatingActionButton
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		retainInstance = true
		val root: View =
			inflater.inflate(R.layout.fragment_tracks_detail, container, false)
		nameTV = root.findViewById(R.id.item_name)
		compTV = root.findViewById(R.id.competitor_price)
		contTV = root.findViewById(R.id.control_price)
		diffTV = root.findViewById(R.id.difference)
		tripped = root.findViewById(R.id.tripped)
		signed = root.findViewById(R.id.signed)
		fab = root.findViewById(R.id.fab)
		fab.setOnClickListener{
			fabClicked()
		}
		return root
	}
	fun setItem(id: String){
		itemId = id
		load()
	}
	fun load(){
		onLoading()
		RequestBuilder(requireContext(), R.string.url_getItemData, Request.Method.GET,
			{
				try {
					val item = Item.deserialize(JSONObject(it))
					nameTV.text = item.name
					itemUpc = item.upc
					compTV.text = "Comp: " + item.competitorPrice.toString()
					contTV.text = "Control: "+ item.controlPrice.toString()
					diffTV.text = (item.competitorPrice?.minus(item.controlPrice!!)).toString()
					tripped.isChecked = item.tripped
					tripped.isClickable = item.tripped
					tripped.setOnClickListener {
						onTrippedClicked()
					}
					signed.isChecked = item.signed
					signed.setOnClickListener{
						onSignedClicked()
					}
					onReady()
				} catch (e: JSONException) {
					e.printStackTrace()
					onError(R.string.error_compares_parse)
				}
			},
			{ onError(R.string.error_compares_network) },
		RequestBuilder.Entry("item_id", itemId)).run()
	}
	fun onTrippedClicked(){
		tripped.isChecked = true
		tripped.isClickable = false
		signed.isClickable = false
		RequestBuilder(requireContext(), R.string.url_tripped, Request.Method.GET,
			{
				val js = JSONObject(it)
				signed.isChecked = js.getBoolean("signed")
				tripped.isChecked = js.getBoolean("tripped")
				signed.isClickable = true
			},
			{
				onToastError(R.string.error_untripping)
				tripped.isClickable = true
				signed.isClickable = true
			}, RequestBuilder.Entry("item_id", itemId)).run()
	}
	fun onSignedClicked(){
		var s = "0"
		if(signed.isChecked)
			s = "1"
		signed.isChecked = !signed.isChecked
		tripped.isClickable = false
		signed.isClickable = false

		RequestBuilder(requireContext(), R.string.url_signed, Request.Method.GET,
			{
				val js = JSONObject(it)
				Log.d("js", js.toString())
				signed.isChecked = js.getBoolean("signed")
				tripped.isChecked = js.getBoolean("tripped")
				signed.isClickable = true
			},
			{
				onToastError(R.string.error_untripping)
				tripped.isClickable = true
				signed.isClickable = true
			}, RequestBuilder.Entry("item_id", itemId),
		RequestBuilder.Entry("signed", s)).run()
	}
	fun addUpc(upc:String){
		/*if(upc.subSequence(0, 7) != itemUpc.subSequence(0, 7)){
			Log.d("Barcode", "Wrong lead")
			Utils.playsound(requireContext(), R.raw.error2)
			onToastError(R.string.error_invalid_upc)
			return
		}*/
		RequestBuilder(requireContext(), R.string.url_addUpc, Request.Method.GET,
			{
				if(it == "created"){
					Log.d("Barcode", "Added barcode")
					Utils.playsound(requireContext(), R.raw.ding)
					onToastError(R.string.added_UPC)}
				else if(it == "ok") {
					Log.d("Barcode", "Network refused UPC")
					Utils.playsound(requireContext(), R.raw.error1)
					onToastError(R.string.upc_not_added)
				}
				resetScanner()
			},
			{ onError(R.string.error_adding_upc)
				Log.e("error",String(it.networkResponse.data, Charsets.UTF_8))
				resetScanner()
			},
			RequestBuilder.Entry("item_id", itemId),
		RequestBuilder.Entry("upc", upc)).run()
	}
	fun fabClicked(){
		(listener as OnFragmentInteractionListener).onFabClicked()
	}
	fun resetScanner(){
		(listener as OnFragmentInteractionListener).onResetScanner()
	}
	interface OnFragmentInteractionListener : net.bobcodes.bobworx.generic.OnFragmentInteractionListener {
		fun onFabClicked()
		fun onResetScanner()
	}
	companion object {
		const val TAG = "TracksDetailFragment"
	}
}