package com.pagarplus.app.modules.advertise.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowAdvertiselistBinding
import com.pagarplus.app.modules.advertise.data.model.AdvertiseRowModel
import java.text.SimpleDateFormat
import java.util.*

class AdvertiseAdapter(
  var list: List<AdvertiseRowModel>
) : RecyclerView.Adapter<AdvertiseAdapter.RowAdvertiseVH>() {
  private var clickListener: OnItemClickListener? = null

  public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowAdvertiseVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_advertiselist,parent,false)
    return RowAdvertiseVH(view)
  }

  public override fun onBindViewHolder(holder: RowAdvertiseVH, position: Int) {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val d = Date()
    val currentDate = sdf.format(Date())

    val advertiseRowModel = list[position]
    holder.binding.advertiseRowModel = advertiseRowModel

    val frmDate = sdf.parse(advertiseRowModel.txtFromDate)
    val toDate = sdf.parse(advertiseRowModel.txtToDate)

    if(advertiseRowModel.txtMessage.isNullOrEmpty()){
      holder.binding.txtMessage.isVisible = false
    }else{
      holder.binding.txtMessage.isVisible = true
    }

    if((d.after(frmDate) && (d.before(toDate)))
      || (currentDate.equals(sdf.format(frmDate)) || currentDate.equals(sdf.format(toDate)))){
      holder.binding.imgEdit.isVisible = true
      if(advertiseRowModel.txtActivestatus == true){
        holder.binding.txtActive.isVisible = false
        holder.binding.imgDeactive.isVisible = true
      }else{
        holder.binding.txtActive.isVisible = true
        holder.binding.imgDeactive.isVisible = false
      }
    }else{
      holder.binding.imgEdit.isVisible = false
      holder.binding.txtActive.isVisible = false
      holder.binding.imgDeactive.isVisible = false
    }

    if(advertiseRowModel.txtBranch.isNullOrEmpty() || advertiseRowModel.txtBranch.equals(" ")){
      advertiseRowModel.txtBranch = advertiseRowModel.organizationname
    }else{

    }

    if(advertiseRowModel.txtDept.isNullOrEmpty()){
      holder.binding.txtDept.isVisible = false
    }else{
      holder.binding.txtDept.isVisible = true
    }
  }

  public override fun getItemCount(): Int = list.size
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<AdvertiseRowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  // method for filtering our recyclerview items.
  fun filterList(filterlist: ArrayList<AdvertiseRowModel>) {
    // below line is to add our filtered
    // list in our employee array list.
    list = filterlist
    // below line is to notify our adapter
    // as change in recycler view data.
    notifyDataSetChanged()
  }

  interface OnItemClickListener {
    fun onItemClick(
      view: View,
      position: Int,
      item: AdvertiseRowModel
    ) {

    }
  }

  inner class RowAdvertiseVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowAdvertiselistBinding = RowAdvertiselistBinding.bind(itemView)
    init {
      binding.imgEdit.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, AdvertiseRowModel())
      }
      binding.txtActive.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, AdvertiseRowModel())
      }
      binding.imgDeactive.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, AdvertiseRowModel())
      }
      binding.imageNoti.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, AdvertiseRowModel())
      }
    }
  }
}
