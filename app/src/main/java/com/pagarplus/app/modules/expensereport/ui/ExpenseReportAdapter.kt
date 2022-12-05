package com.pagarplus.app.modules.expensereport.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowExpenseReportBinding
import com.pagarplus.app.databinding.RowExpenseWiseReportBinding
import com.pagarplus.app.databinding.RowUserExpenseItemBinding
import com.pagarplus.app.network.models.expense.ExpenseRowModel
import kotlin.Int
import kotlin.Unit

public class ExpenseReportAdapter(public var mlist: List<ExpenseRowModel>, val context:Context,var reportHolderType:Int=0) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
  Filterable {
  private var clickListener: OnItemClickListener? = null
  var reportFilterList = ArrayList<ExpenseRowModel>()
  var list = ArrayList<ExpenseRowModel>()
  init {
      list=mlist as ArrayList<ExpenseRowModel>
    reportFilterList=mlist as ArrayList<ExpenseRowModel>
  }
  public fun updateData(newData: ArrayList<ExpenseRowModel>): Unit {
    if(reportFilterList.size>0){
      notifyItemRangeRemoved(0, reportFilterList.size);
      reportFilterList.clear()
    }
    if(list.size>0)
    {  notifyItemRangeRemoved(0, list.size);
      list.clear()
    }

    list = newData
    reportFilterList=newData
    notifyItemRangeChanged(0, newData.size);
    notifyDataSetChanged()
  }
  public fun setOnItemClickListener(clickListener: OnItemClickListener): Unit {
    this.clickListener = clickListener
  }

  public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    if(reportHolderType==1){
      val view=LayoutInflater.from(parent.context).inflate(R.layout.row_user_expense_item,parent,false)
      return RowUserWiseVH(view,clickListener!!)
    }
   else if(reportHolderType==2){
      val view=LayoutInflater.from(parent.context).inflate(R.layout.row_expense_wise_report,parent,false)
      return RowExpenseWiseVH(view,clickListener!!)
    }else{
      val view=LayoutInflater.from(parent.context).inflate(R.layout.row_expense_report,parent,false)
      return RowLedger2VH(view,clickListener!!)
    }

  }

  public override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int): Unit {
    var ledger2RowModel = ExpenseRowModel()
    ledger2RowModel = reportFilterList[position]
    if(holder is RowLedger2VH)
     holder.binding.expensReportRow = ledger2RowModel
      else if(holder is RowUserWiseVH)
      holder.binding.expensReportRow = ledger2RowModel
    else if(holder is RowExpenseWiseVH)
      holder.binding.expenseTypeReportRow = ledger2RowModel
  }

  public override fun getItemCount(): Int = reportFilterList.size//list.size
  public interface OnItemClickListener {
    public fun onItemClick(view: View, position: Int, item: ExpenseRowModel): Unit {
    }
  }

  public inner class RowLedger2VH(view: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(view)
  {
    public val binding: RowExpenseReportBinding = RowExpenseReportBinding.bind(itemView)
    init {
        binding.travelCard.setOnClickListener {
          listener.onItemClick(it,adapterPosition,reportFilterList[adapterPosition])
        }
    }
}

  public inner class RowUserWiseVH(view: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(view)
  {
    public val binding: RowUserExpenseItemBinding = RowUserExpenseItemBinding.bind(itemView)
    init {
      binding.travelCard.setOnClickListener {
        listener.onItemClick(it,adapterPosition,reportFilterList[adapterPosition])
      }
    }
  }

  public inner class RowExpenseWiseVH(view: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(view)
  {
    public val binding: RowExpenseWiseReportBinding = RowExpenseWiseReportBinding.bind(itemView)
    init {
      binding.expenseCard.setOnClickListener {
        listener.onItemClick(it,adapterPosition,reportFilterList[adapterPosition])
      }
    }
  }

  override fun getFilter(): Filter {

    return object : Filter()
    {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val charSearch: String = constraint.toString()

        if (charSearch.isEmpty()) {
          reportFilterList = list as ArrayList<ExpenseRowModel>
        } else {
          val resultList = ArrayList<ExpenseRowModel>()

          for (row in list) {
            if (row.EmployeeName?.contains(charSearch, true)!! ||
              row.AmountValue?.contains(charSearch, true)!! ||
              row.ExpenseDateTime?.contains(charSearch, true)!! ||
              row.Status?.contains(charSearch, true)!!
            )
              {
              resultList.add(row)
            }
          }
          reportFilterList = resultList

        }
        val filterResults = FilterResults()
        filterResults.values = reportFilterList
        Log.e("Filter...", "OnFilter...$reportFilterList")
        return filterResults
      }

      @Suppress("UNCHECKED_CAST")
      override fun publishResults(constraint: CharSequence?, results: FilterResults) {

        Log.e("finallist..","list...${results.values}")
        reportFilterList = results.values as ArrayList<ExpenseRowModel>
       if(reportFilterList.size==0)

        Log.e("Filter...", "OnPublish...$reportFilterList")
        notifyDataSetChanged()
      }
    }
  }
}
