package at.oltdaniel.rwthmensa

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class DishAdapter(
    private val data: ArrayList<DayMenu>
) : RecyclerView.Adapter<DishAdapter.ViewHolder>() {
    private var day = 0

    class ViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_dishes, parent, false) as LinearLayout
        return ViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val labelView = holder.linearLayout.findViewById<TextView>(R.id.dish_label)
        val priceView = holder.linearLayout.findViewById<TextView>(R.id.dish_price)
        val foodView = holder.linearLayout.findViewById<TextView>(R.id.dish_food)
        val dish = data[day].dishes[position]
        labelView.text = dish.label
        priceView.text = dish.getPrice()
        foodView.text = dish.food
    }

    override fun getItemCount() : Int {
        if(data.size < (day + 1)) {
            return 0
        }
        return data[day].dishes.size
    }

    fun selectToday() {
        data.forEachIndexed { index, dayMenu ->
            if(dayMenu.date.toDaysOnly() == Date().toDaysOnly()) {
                day = index
                this.notifyDataSetChanged()
                return
            } else if(dayMenu.date.after(Date())) {
                day = index
                this.notifyDataSetChanged()
                return
            }
        }
    }

    fun getCurrentDay() : DayMenu {
        return data[day]
    }

    fun previousDay() : Boolean {
        if(day > 0) {
            day -= 1
            this.notifyDataSetChanged()
        }
        return (day > 0)
    }

    fun previousEnabled() = (day > 0)

    fun nextDay() : Boolean {
        if(day < data.size) {
            day += 1
            this.notifyDataSetChanged()
        }
        return (day < data.size - 1)
    }

    fun nextEnabled() = (day < data.size - 1)
}