package net.bobcodes.bobworx.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.bobcodes.bobworx.R
import net.bobcodes.bobworx.tracks.TracksActivity

class MainActivity: AppCompatActivity(){
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		findViewById<Button>(R.id.main_tracks_bt).setOnClickListener {
			startActivity(Intent(this, TracksActivity::class.java))
		}
	}
}