package com.pagarplus.app.modules.adminemplist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowDetailsBinding
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.modules.notificationcreatemessage.data.model.Details2RowModel
import kotlin.Int
import kotlin.collections.List

class DetailsAdapter(
  var list: List<DetailsRowModel>
) : RecyclerView.Adapter<DetailsAdapter.RowDetailsVH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowDetailsVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_details,parent,false)
    return RowDetailsVH(view)
  }

  override fun onBindViewHolder(holder: RowDetailsVH, position: Int) {
    val detailsRowModel = list[position]
    holder.binding.detailsRowModel = detailsRowModel
    if(detailsRowModel.txtBranch.isNullOrEmpty()){
      detailsRowModel.txtBranch = detailsRowModel.organizationname
    }else{

    }

    if(detailsRowModel.status == true){
      holder.binding.frameDeactivate.isVisible = true
      holder.binding.frameActivate.isVisible = false
      holder.binding.frameEdit.isVisible = true
      holder.binding.frameShare.isVisible = true
    }else{
      holder.binding.frameDeactivate.isVisible = false
      holder.binding.frameActivate.isVisible = true
      holder.binding.frameEdit.isVisible = false
      holder.binding.frameShare.isVisible = false
    }
  }

  override fun getItemCount(): Int = list.size
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<DetailsRowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  // method for filtering our recyclerview items.
  fun filterList(filterlist: ArrayList<DetailsRowModel>) {
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
      item: DetailsRowModel
    ) {
    }
  }

  inner class RowDetailsVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowDetailsBinding = RowDetailsBinding.bind(itemView)
    init {
      binding.frameEdit.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, DetailsRowModel())
      }
      binding.frameDeactivate.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, DetailsRowModel())
      }
      binding.frameShare.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, DetailsRowModel())
      }
      binding.frameActivate.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, DetailsRowModel())
      }
    }
  }
}
