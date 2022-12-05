package com.pagarplus.app.modules.itemlistdialog.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowItemlistdialog1Binding
import com.pagarplus.app.modules.itemlistdialog.data.model.Itemlistdialog1RowModel
import java.lang.Exception
import kotlin.Int
import kotlin.Unit
import kotlin.collections.List

public class RecyclerItemlistdialogAdapter(
  public var mlist: List<Itemlistdialog1RowModel>
) : RecyclerView.Adapter<RecyclerItemlistdialogAdapter.RowItemlistdialog1VH>(),Filterable{
  private var clickListener: OnItemClickListener? = null

  var ItemList = arrayListOf<Itemlistdialog1RowModel>()
  var list  = arrayListOf<Itemlistdialog1RowModel>()

  init {
    ItemList = mlist as ArrayList<Itemlistdialog1RowModel>
    list = mlist as ArrayList<Itemlistdialog1RowModel>
  }
  public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowItemlistdialog1VH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_itemlistdialog1,parent,false)
    return RowItemlistdialog1VH(view)
  }

  public override fun onBindViewHolder(holder: RowItemlistdialog1VH, position: Int): Unit {
    val itemlistdialog1RowModel = ItemList[position]
    holder.binding.itemlistdialog1RowModel = itemlistdialog1RowModel
  }

  public override fun getItemCount(): Int = ItemList.size

  public fun updateData(newData: ArrayList<Itemlistdialog1RowModel>): Unit {
    if (ItemList!!.size > 0) {
      ItemList!!.clear()
      notifyItemRangeRemoved(0, ItemList!!.size);
    }
    if (list!!.size > 0) {
      list!!.clear()
      notifyItemRangeRemoved(0, list!!.size);
    }
    list = newData
    ItemList = newData
    // notifyItemRangeChanged(0, newData.size);
    notifyDataSetChanged()
  }

  public fun setOnItemClickListener(clickListener: OnItemClickListener): Unit {
    this.clickListener = clickListener
  }
  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val charSearch = constraint.toString()
        if (charSearch.isEmpty()) {
          ItemList = list as ArrayList<Itemlistdialog1RowModel>
        } else{
          val resultList = ArrayList<Itemlistdialog1RowModel>()

          for (row in list!!) {
            if (row.txtName?.contains(charSearch, true)!!) {
              resultList.add(row)
            }
          }
          ItemList = resultList
          Log.e("FilterAdapter", "$ItemList")
        }
        val filterResults = FilterResults()
        filterResults.values = ItemList
        return filterResults
      }

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        try {
          ItemList = results?.values as ArrayList<Itemlistdialog1RowModel>
          Log.e("OnPublished...", "$ItemList")
          notifyDataSetChanged()
        } catch (Ex: Exception) {
          Log.e("OnPublished...", Ex.toString())
        }
      }
    }
  }

  public interface OnItemClickListener {
    public fun onItemClick(
      view: View,
      position: Int,
      item: Itemlistdialog1RowModel
    ): Unit {
    }
  }

  public inner class RowItemlistdialog1VH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    public val binding: RowItemlistdialog1Binding = RowItemlistdialog1Binding.bind(itemView)
    init {
      binding.linearGroup43.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, ItemList[adapterPosition])
      }
    }
  }
}
