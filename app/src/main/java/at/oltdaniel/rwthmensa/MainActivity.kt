package at.oltdaniel.rwthmensa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var layout: View

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
            if(!viewAdapter.previousDay()) {
                previousDayButton.isEnabled = false
            } else if(!nextDayButton.isEnabled) {
                nextDayButton.isEnabled = true
            }
            renderDay(viewAdapter.getCurrentDay())
        }
        nextDayButton.setOnClickListener {
            if(!viewAdapter.nextDay()) {
                nextDayButton.isEnabled = false
            } else if(!previousDayButton.isEnabled) {
                previousDayButton.isEnabled = true
            }
            renderDay(viewAdapter.getCurrentDay())
        }
        headingTextView.setOnClickListener {
            viewAdapter.selectToday()
            renderDay(viewAdapter.getCurrentDay())
        }

        Thread(Runnable {
            val totalMenu = crawler.getTotalMenu()
            if(totalMenu == null) {
                runOnUiThread {
                    Snackbar.make(content, "Loading error", Snackbar.LENGTH_INDEFINITE).show()
                }
            } else {
                days.addAll(totalMenu.days)
                runOnUiThread {
                    viewAdapter.selectToday()
                    renderDay(viewAdapter.getCurrentDay())
                }
            }
            runOnUiThread { progressBar.visibility = View.INVISIBLE }
        }).start()
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
