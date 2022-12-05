package com.yuvapagar.app.modules.notificationcreatemessage.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowDetails2Binding
import com.pagarplus.app.modules.notification.data.model.MessageRowModel
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel

class DetailsAdapter2(
  var list: List<Details2RowModel>
) : RecyclerView.Adapter<DetailsAdapter2.RowDetails2VH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowDetails2VH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_details2,parent,false)
    return RowDetails2VH(view)
  }

  override fun onBindViewHolder(holder: RowDetails2VH, position: Int) {
    val details2RowModel = list[position]
    holder.binding.details2RowModel = details2RowModel
  }

  override fun getItemCount(): Int = list.size
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<Details2RowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  // method for filtering our recyclerview items.
  fun filterList(filterlist: ArrayList<Details2RowModel>) {
    // below line is to add our filtered
    // list in our employee array list.
    list = filterlist
    // below line is to notify our adapter
    // as change in recycler view data.
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  interface OnItemClickListener {
    fun onItemClick(
      view: View,
      position: Int,
      item: Details2RowModel
    ) {
    }
  }

  inner class RowDetails2VH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: RowDetails2Binding = RowDetails2Binding.bind(itemView)
    init {
      binding.chkEmplist.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, Details2RowModel())
      }
      binding.txtDeptNamelist.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, Details2RowModel())
      }
    }
  }
}
