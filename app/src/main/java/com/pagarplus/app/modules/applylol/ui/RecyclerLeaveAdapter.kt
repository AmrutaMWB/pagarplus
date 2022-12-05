package com.pagarplus.app.modules.applylol.ui

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
import com.pagarplus.app.databinding.RowLeaveItemBinding
import com.pagarplus.app.modules.applylol.data.model.LeaveModel
import kotlin.Int
import kotlin.Unit
import kotlin.collections.List

public class RecyclerLeaveAdapter( var mlist: List<LeaveModel>,var context: Context) : RecyclerView.Adapter<RecyclerLeaveAdapter.RowLeaveVH>() {
    private var clickListener: OnItemClickListener? = null
    var list: ArrayList<LeaveModel>
    var FilterList: ArrayList<LeaveModel>

    init {
        list = mlist as ArrayList<LeaveModel>
        FilterList = mlist as ArrayList<LeaveModel>
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowLeaveVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_leave_item, parent, false)
        return RowLeaveVH(view, clickListener!!)
    }

    public override fun onBindViewHolder(holder: RowLeaveVH, position: Int): Unit {
        val leaveModel = FilterList[position]
        holder.binding.leaveRowModel = leaveModel
    }

    public override fun getItemCount(): Int = FilterList.size

    public fun updateData(newData: ArrayList<LeaveModel>): Unit {
        FilterList = newData
        list=newData
        notifyDataSetChanged()
        Log.e("List","$newData")
    }

    public fun setOnItemClickListener(clickListener: OnItemClickListener): Unit {
        this.clickListener = clickListener
    }

    public interface OnItemClickListener {
        public fun onItemClick(
            view: View,
            position: Int,
            item: LeaveModel
        ): Unit {
        }
    }

   public fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch: String = constraint.toString()

                if (charSearch.isEmpty()) {
                    FilterList = list as ArrayList<LeaveModel>
                    Log.e("onSpace...", "pricelist$FilterList")
                } else {
                    val resultList = ArrayList<LeaveModel>()

                    for (row in list) {
                        if (row.ReasonForLeave?.contains(charSearch, true)!!
                            || row.ApproveStatus?.contains(charSearch, true)!!
                            || row.LeaveType?.contains(charSearch, true)!!
                            || row.RequestDate?.contains(charSearch, true)!!
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
                FilterList = results?.values as ArrayList<LeaveModel>
                if (FilterList.size == 0)
                    Toast.makeText(context, "Item Not Found", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }
    }

    public inner class RowLeaveVH(view: View, listener: RecyclerLeaveAdapter.OnItemClickListener) : RecyclerView.ViewHolder(view) {
        public val binding: RowLeaveItemBinding = RowLeaveItemBinding.bind(itemView)

        init {
            binding.constraintGroup.setOnClickListener {
                listener.onItemClick(it, adapterPosition, FilterList[adapterPosition])
            }
        }
    }
}


