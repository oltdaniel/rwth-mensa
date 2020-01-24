package at.oltdaniel.rwthmensa

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DishAdapter(
    private val data: ArrayList<DayMenu>
) : RecyclerView.Adapter<DishAdapter.ViewHolder>() {
    var day = 0
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
}