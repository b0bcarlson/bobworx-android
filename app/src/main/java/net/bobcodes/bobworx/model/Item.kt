package net.bobcodes.bobworx.model

import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.lang.Exception

@Serializable
class Item : Model("item", "items") {
	companion object{
	fun deserialize(jsonObject: JSONObject): Item {
		val item = Item()
		if (jsonObject.has("id")){
			item.id = jsonObject.getString("id")
		}
		if (jsonObject.has("name")){
			item.name = jsonObject.getString("name")
		}
		if(jsonObject.has("active")){
			item.active = jsonObject.getBoolean("active")
		}
		if(jsonObject.has("signed")){
			item.signed = jsonObject.getBoolean("signed")
		}
		if(jsonObject.has("tripped")){
			item.tripped = jsonObject.getBoolean("tripped")
		}
		if(jsonObject.has("group")){
			item.groupName = jsonObject.getString("group")
		}
		if(jsonObject.has("upc")){
			item.upc = jsonObject.getString("upc")
		}
		if(jsonObject.has("stale")){
			item.stale = jsonObject.getBoolean("stale")
		}
		try {
			if (jsonObject.has("control_price")) {
				item.controlPrice = jsonObject.getInt("control_price")
			}
			if (jsonObject.has("competitor_price")) {
				item.competitorPrice = jsonObject.getInt("competitor_price")
			}
			item.difference = item.competitorPrice!! - item.controlPrice!!
		}
		catch (exception:Exception){}
		if(jsonObject.has("target")){
			item.target = jsonObject.getInt("target")
		}

		return item
	}
	}

	lateinit var name : String
	var active: Boolean = false
	var signed: Boolean = false
	var tripped: Boolean = false
	lateinit var groupName: String
	var controlPrice: Int? = null
	var competitorPrice: Int? = null
	var difference: Int? = null
	var target: Int = 0
	lateinit var upc: String
	var stale: Boolean = false
}