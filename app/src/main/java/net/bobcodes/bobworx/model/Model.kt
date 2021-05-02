package net.bobcodes.bobworx.model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.bobcodes.bobworx.R
import java.util.*

@Serializable
open class Model(@Transient val NAME: String = "", @Transient val NAME_PLURAL: String = "") {
    lateinit var id: String
    class ItemAdapter<T>: ArrayAdapter<T> {
        var layout: Int = 0
        var viewLayout: ViewLayout<T>

        constructor(context: Context, resource: Int, items: List<T>, layout: Int,
        viewLayout: ViewLayout<T>) : super(
            context,
            resource,
            items
        ){
            this.layout = layout
            this.viewLayout = viewLayout
        }
        interface ViewLayout<T> {
            fun run(root: View, modelInstance: T)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val modelInstance: T = getItem(position)!!
            val inflater =
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            @SuppressLint("ViewHolder", "InflateParams") val view: View =
                Objects.requireNonNull(inflater).inflate(layout, null)
            viewLayout.run(view, modelInstance)
            return view
        }
    }
}
