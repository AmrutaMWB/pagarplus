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
import com.pagarplus.app.databinding.RowLoanItemBinding
import com.pagarplus.app.modules.applylol.data.model.LoanModel
import kotlin.Int
import kotlin.Unit
import kotlin.collections.List

public class RecyclerLoanAdapter( var mlist: List<LoanModel>,var context: Context) : RecyclerView.Adapter<RecyclerLoanAdapter.RowLoanVH>() {
    private var clickListener: OnItemClickListener? = null
    var list: ArrayList<LoanModel>
    var FilterList: ArrayList<LoanModel>

    init {
        list = mlist as ArrayList<LoanModel>
        FilterList = mlist as ArrayList<LoanModel>
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowLoanVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_loan_item, parent, false)
        return RowLoanVH(view, clickListener!!)
    }

    public override fun onBindViewHolder(holder: RowLoanVH, position: Int): Unit {
        val loanModel = FilterList[position]
        holder.binding.loanRowModel = loanModel
    }

    public override fun getItemCount(): Int = FilterList.size

    public fun updateData(newData: ArrayList<LoanModel>): Unit {
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
            item: LoanModel
        ): Unit {
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch: String = constraint.toString()

                if (charSearch.isEmpty()) {
                    FilterList = list as ArrayList<LoanModel>
                } else {
                    val resultList = ArrayList<LoanModel>()

                    for (row in list) {
                        if (row.RequestedAmount?.contains(charSearch, true)!!
                            || row.ApproveStatus?.contains(charSearch, true)!!
                            || row.RequestDate?.contains(charSearch, true)!!
                            || row.ReasonForLoan?.contains(charSearch, true)!!
                        ) {
                            resultList.add(row)
                        }
                    }
                    FilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = FilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                FilterList = results?.values as ArrayList<LoanModel>
                if (FilterList.size == 0)
                    Toast.makeText(context, "Item Not Found", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }
    }

    public inner class RowLoanVH(view: View, listener: RecyclerLoanAdapter.OnItemClickListener) : RecyclerView.ViewHolder(view) {
        public val binding: RowLoanItemBinding = RowLoanItemBinding.bind(itemView)

        init {
            binding.constraintGroup.setOnClickListener {
                listener.onItemClick(it, adapterPosition, FilterList[adapterPosition])
            }
        }
    }
}


