package net.bobcodes.bobworx.tracks

import android.graphics.Color.green
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.RequestBuilder
import net.bobcodes.bobworx.generic.GenericFragmentWithListener
import net.bobcodes.bobworx.model.Item
import net.bobcodes.bobworx.model.Model
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception


class TracksFragment : GenericFragmentWithListener(TAG) {
	private lateinit var listView: ListView
	private lateinit var items: ArrayList<Item>
	private lateinit var adapter: Model.ItemAdapter<Item>
	private lateinit var fab: FloatingActionButton
	private lateinit var upcs: HashMap<String, String>
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		retainInstance = true
		val root: View =
			inflater.inflate(R.layout.fragment_generic_list, container, false)
		listView = root.findViewById(R.id.generic_list_lv)
		fab = root.findViewById(R.id.generic_list_fab)
		fab.setOnClickListener {
			onOpenScanner()
		}
		return root
	}

	fun getCompares(){
		onLoading()
		RequestBuilder(requireContext(), R.string.url_getItems, Request.Method.GET,
			{
				try {
					val result = JSONArray(it)
					items = ArrayList()
					upcs = HashMap()
					for (i in 0 until result.length()) {
						val obj = result.getJSONObject(i)
						items.add(Item.deserialize(obj))
						for(u in 0 until obj.getJSONArray("upcs").length())
							upcs[obj.getJSONArray("upcs").getJSONObject(u).getString("upc")] = obj.getString("id")
					}
					val obj = object : Model.ItemAdapter.ViewLayout<Item>{
						override fun run(root: View, modelInstance: Item) {
							root.findViewById<TextView>(R.id.list_item_item_name_tv).text = modelInstance.name
							if (!modelInstance.active)
								root.findViewById<TextView>(R.id.list_item_item_name_tv).setTypeface(null, Typeface.ITALIC)
							root.findViewById<CheckBox>(R.id.checkbox_tripped).isChecked = modelInstance.tripped
							root.findViewById<CheckBox>(R.id.checkbox_signed).isChecked = modelInstance.signed
							root.findViewById<TextView>(R.id.list_item_group_name).text = modelInstance.groupName
							root.findViewById<TextView>(R.id.control_price_tv).text = if (modelInstance.stale) "STALE" else modelInstance.controlPrice.toString()
							root.findViewById<TextView>(R.id.competitor_price_tv).text = if (modelInstance.stale) "STALE" else modelInstance.competitorPrice.toString()
							root.findViewById<TextView>(R.id.difference_tv).text = if (modelInstance.stale) "STALE" else modelInstance.difference.toString()
							try{
								when {
									modelInstance.stale -> root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightgrey))
									modelInstance.difference!! >= modelInstance.target -> root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightgreen))
									modelInstance.difference!!>=0 -> root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
									else -> root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightpink))
								}
							}catch (exception:Exception){}
						}
					}
					if(isAdded) {
						adapter = Model.ItemAdapter(
							requireContext(),
							R.layout.list_item_item,
							items,
							R.layout.list_item_item,
							obj
						)
						listView.setOnItemClickListener { _, _, position, _ ->
							Log.d("TracksFragment", "Item selected")
							onItemSelected(items[position])
						}
						listView.adapter = adapter
						onUpcs(upcs)
						onReady()
					}
				} catch (e: JSONException) {
					e.printStackTrace()
					onError(R.string.error_compares_parse)
				}
			},
			{ onError(R.string.error_compares_network) }).run()
	}
	private fun onOpenScanner(){
		(listener as OnFragmentInteractionListener).onOpenScanner()
	}
	private fun onItemSelected(item: Item){
		(listener as OnFragmentInteractionListener).onItemSelected(item)
	}
	private fun onUpcs(upcs: HashMap<String,String>){
		(listener as OnFragmentInteractionListener).onUpcs(upcs)
	}
	interface OnFragmentInteractionListener : net.bobcodes.bobworx.generic.OnFragmentInteractionListener {
		fun onOpenScanner()

		fun onItemSelected(item: Item)

		fun onUpcs(upcs: HashMap<String, String>)
	}
	companion object {
		const val TAG = "TracksFragment"
	}

}