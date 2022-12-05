package com.pagarplus.app.modules.workholidays.ui

import com.pagarplus.app.network.models.holiday.HolidayItem
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowHolidayitemBinding
import kotlin.Int
import kotlin.Unit
import kotlin.collections.List

public class RecyclerHolidayAdapter( var mlist: List<HolidayItem>,var context: Context) : RecyclerView.Adapter<RecyclerHolidayAdapter.RowHolidayVH>() {
    private var clickListener: OnItemClickListener? = null
    var list: ArrayList<HolidayItem>
    var FilterList: ArrayList<HolidayItem>

    init {
        list = mlist as ArrayList<HolidayItem>
        FilterList = mlist as ArrayList<HolidayItem>
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolidayVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_holidayitem, parent, false)
        return RowHolidayVH(view, clickListener!!)
    }

    public override fun onBindViewHolder(holder: RowHolidayVH, position: Int): Unit {
        val holidayModel = FilterList[position]
       if(holidayModel.date?.contains("T")!!)
           holidayModel.date=holidayModel.date!!.split("T")[0]
        holder.binding.holidayRowModel = holidayModel
    }

    public override fun getItemCount(): Int = FilterList.size

    public fun updateData(newData: ArrayList<HolidayItem>): Unit {
        FilterList = newData
        list=newData
        notifyDataSetChanged()
    }

    public fun setOnItemClickListener(clickListener: OnItemClickListener): Unit {
        this.clickListener = clickListener
    }

    public interface OnItemClickListener {
        public fun onItemClick(
            view: View,
            position: Int,
            item: HolidayItem
        ): Unit {
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch: String = constraint.toString()

                if (charSearch.isEmpty()) {
                    FilterList = list as ArrayList<HolidayItem>
                    Log.e("onSpace...", "pricelist$FilterList")
                } else {
                    val resultList = ArrayList<HolidayItem>()

                    for (row in list!!) {
                        if (row.date?.contains(charSearch, true)!!
                            || row.comment?.contains(charSearch, true)!!
                        ) {
                            resultList.add(row)
                        }
                    }
                    FilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = FilterList
                // Log.e("Filter...", "OnFilter...$priceFilterList")
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                FilterList = results?.values as ArrayList<HolidayItem>
                if (FilterList.size == 0)
                    Toast.makeText(context, "Item Not Found", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }
    }

    public inner class RowHolidayVH(view: View, listener: RecyclerHolidayAdapter.OnItemClickListener) : RecyclerView.ViewHolder(view) {
        public val binding: RowHolidayitemBinding = RowHolidayitemBinding.bind(itemView)

        init {
            binding.btnDelete.setOnClickListener {
                listener.onItemClick(it, adapterPosition, FilterList[adapterPosition])
            }
        }
    }
}


