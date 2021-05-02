package net.bobcodes.bobworx.generic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import net.bobcodes.bobworx.R
import java.util.*
import kotlin.collections.HashMap

public abstract class GenericActivity : AppCompatActivity() {
	private lateinit var fragmentManager: FragmentManager;
	protected lateinit var fragments: HashMap<String, GenericFragment>

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if(item.itemId == android.R.id.home){
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	protected fun onCreate(savedInstanceState: Bundle?, vararg fragments: Class<out GenericFragment>): Boolean{
		//returns true if the fragments are new
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_generic)
		fragmentManager = supportFragmentManager
		this.fragments = java.util.HashMap()
		return if (fragmentManager.fragments.size > 0) {
			for (fragment in fragments) {
				try {
					this.fragments[fragment.getField("TAG")[null].toString()] = fragmentManager.findFragmentByTag(fragment.getField("TAG")[null].toString()) as GenericFragment
				} catch (e: IllegalAccessException) {
					e.printStackTrace()
				} catch (e: NoSuchFieldException) {
					e.printStackTrace()
				}
			}
			false
		} else {
			var transaction = fragmentManager.beginTransaction()
			for (fragment in fragments) {
				try {
					this.fragments[fragment.getField("TAG")[null].toString()] = fragment.newInstance()
					transaction = transaction.add(R.id.generic_container, this.fragments[fragment.getField("TAG")[null].toString()]!!, fragment.getField("TAG")[null].toString()).hide(
						this.fragments[fragment.getField("TAG")[null].toString()]!!)
				} catch (e: IllegalAccessException) {
					e.printStackTrace()
				} catch (e: InstantiationException) {
					e.printStackTrace()
				} catch (e: NoSuchFieldException) {
					e.printStackTrace()
				}
			}
			try {
				transaction.show(this.fragments[fragments[0].getField("TAG")[null].toString()]!!).commitNow()
			} catch (e: IllegalAccessException) {
				e.printStackTrace()
			} catch (e: NoSuchFieldException) {
				e.printStackTrace()
			}
			true
		}
	}
	protected fun switchFragment(fragment: GenericFragment) {
		Handler().post {
			var t = fragmentManager.beginTransaction()
			for (f in fragments.values) {
				t = t.hide(f)
			}
			t.show(fragment).commitNow()
		}
	}
	protected fun getFragment(TAG: String): GenericFragment {
		return fragments[TAG]!!
	}

	protected val visibleFragment: String
		get() {
			for (f in fragments.values) if (!f.isHidden) return f.TAG
			return ""
		}

	fun onFragmentLoading() {
		switchFragment(getFragment(LoadingFragment.TAG))
	}

	open fun onFragmentReady(fragment: GenericFragment) {
		switchFragment(fragment)
	}

	fun onFragmentError(error: Int) {
		(getFragment(TextFragment.TAG) as TextFragment?)!!.setText(error)
	}

	fun onNonInterruptingError(fragment: GenericFragment, error: Int) {
		if (visibleFragment == LoadingFragment.TAG) (getFragment(TextFragment.TAG) as TextFragment?)!!.setText(error) else switchFragment(fragment)
		Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
	}
}