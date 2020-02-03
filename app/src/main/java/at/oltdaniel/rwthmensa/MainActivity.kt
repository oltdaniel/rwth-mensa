package at.oltdaniel.rwthmensa

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var layout: View

    private lateinit var sharedPreferences: SharedPreferences
    private val NIGHT_MODE = "night_mode"

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: DishAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var headingTextView: TextView
    private lateinit var extraMainTextView: TextView
    private lateinit var extraSecondTextView: TextView
    private lateinit var nextDayButton: Button
    private lateinit var previousDayButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("mensa-plan", Context.MODE_PRIVATE)
        val current = sharedPreferences.getBoolean(NIGHT_MODE, false)
        if(current) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        } else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.content)
        loadingProgressBar = findViewById(R.id.progressBar)
        headingTextView = findViewById(R.id.day_overview_date)
        extraMainTextView = findViewById(R.id.day_overview_extras_main)
        extraSecondTextView = findViewById(R.id.day_overview_extras_second)
        nextDayButton = findViewById(R.id.button_next_day)
        previousDayButton = findViewById(R.id.button_previous_day)

        val crawler = Crawler(this)
        val days = ArrayList<DayMenu>()

        viewManager = LinearLayoutManager(this)
        viewAdapter = DishAdapter(days)
        recyclerView = findViewById<RecyclerView>(R.id.day_overview_dishes).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        previousDayButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "previous_day")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            if(!viewAdapter.previousDay()) {
                previousDayButton.isEnabled = false
            } else if(!nextDayButton.isEnabled) {
                nextDayButton.isEnabled = true
            }
            renderDay(viewAdapter.getCurrentDay())
        }
        nextDayButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "next_day")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            if(!viewAdapter.nextDay()) {
                nextDayButton.isEnabled = false
            } else if(!previousDayButton.isEnabled) {
                previousDayButton.isEnabled = true
            }
            renderDay(viewAdapter.getCurrentDay())
        }
        headingTextView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "today")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            viewAdapter.selectToday()
            renderDay(viewAdapter.getCurrentDay())
        }

        Thread(Runnable {
            val bundle = Bundle()
            val totalMenu = crawler.getTotalMenu()
            if(totalMenu == null) {
                runOnUiThread {
                    Snackbar.make(content, "Loading error", Snackbar.LENGTH_INDEFINITE).show()
                }
                bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, false)
            } else {
                days.addAll(totalMenu.days)
                runOnUiThread {
                    viewAdapter.selectToday()
                    renderDay(viewAdapter.getCurrentDay())
                }
                bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, true)
            }
            runOnUiThread { progressBar.visibility = View.INVISIBLE }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(sharedPreferences.getBoolean(NIGHT_MODE, false) && menu != null) {
            val item = menu.findItem(R.id.night_mode)
            if(item != null) {
                item.icon = getDrawable(R.drawable.ic_night_mode)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.night_mode -> {
                setNightModeStatus(item, 2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setNightModeStatus(item: MenuItem, status: Int) {
        when(status) {
            2 -> {
                val current = sharedPreferences.getBoolean(NIGHT_MODE, false)
                if(current) {
                    setNightModeStatus(item, 0)
                } else {
                    setNightModeStatus(item, 1)
                }
            }
            0 -> {
                item.icon = getDrawable(R.drawable.ic_night_mode_off)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                sharedPreferences.edit {
                    this.putBoolean(NIGHT_MODE, false)
                }
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "night_mode")
                bundle.putString(FirebaseAnalytics.Param.CONTENT, "day")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            }
            1 -> {
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "night_mode")
                bundle.putString(FirebaseAnalytics.Param.CONTENT, "night")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                item.icon = getDrawable(R.drawable.ic_night_mode)
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                sharedPreferences.edit {
                    this.putBoolean(NIGHT_MODE, true)
                }
            }
        }
    }

    private fun renderDay(day : DayMenu) {
        runOnUiThread {
            headingTextView.text = day.date.toDaysOnly()
            extraMainTextView.text = day.mainExtra.food
            extraSecondTextView.text = day.secondExtra.food
            previousDayButton.isEnabled = viewAdapter.previousEnabled()
            nextDayButton.isEnabled = viewAdapter.nextEnabled()
        }
    }
}
