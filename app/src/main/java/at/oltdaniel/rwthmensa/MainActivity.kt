package at.oltdaniel.rwthmensa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var layout: View

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var headingTextView: TextView
    private lateinit var extraMainTextView: TextView
    private lateinit var extraSecondTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.content)
        loadingProgressBar = findViewById(R.id.progressBar)
        headingTextView = findViewById(R.id.day_overview_date)
        extraMainTextView = findViewById(R.id.day_overview_extras_main)
        extraSecondTextView = findViewById(R.id.day_overview_extras_second)

        val crawler = Crawler(this)
        val days = ArrayList<DayMenu>()

        viewManager = LinearLayoutManager(this)
        viewAdapter = DishAdapter(days)
        recyclerView = findViewById<RecyclerView>(R.id.day_overview_dishes).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
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
                    viewAdapter.notifyDataSetChanged()
                    headingTextView.text = "Heute"
                    extraMainTextView.text = totalMenu.days[0].mainExtra.food
                    extraSecondTextView.text = totalMenu.days[0].secondExtra.food
                    Snackbar.make(
                        content,
                        "Loaded from ${totalMenu.start.toDaysOnly()} to ${totalMenu.end.toDaysOnly()}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            runOnUiThread { progressBar.visibility = View.INVISIBLE }
        }).start()


    }
}
