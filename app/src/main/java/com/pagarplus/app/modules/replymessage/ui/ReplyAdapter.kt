package com.yuvapagar.app.modules.reports.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowDetails1Binding
import com.pagarplus.app.modules.replymessage.data.model.MessageReplyModel
import kotlin.Int
import kotlin.collections.List

class ReplyAdapter(
  var list: List<MessageReplyModel>
) : RecyclerView.Adapter<ReplyAdapter.RowDetails1VH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowDetails1VH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_details1,parent,false)
    return RowDetails1VH(view)
  }

  override fun onBindViewHolder(holder: RowDetails1VH, position: Int) {
    val details1RowModel = list[position]
    holder.binding.details1RowModel = details1RowModel

    /*if(list.size > 1) {
      if (details1RowModel.txtCommonDate?.get(position)!!
          .equals(details1RowModel.txtCommonDate?.get(position)!! + 1)) {
        holder.binding.textcomdate.isVisible = false
      } else {
        holder.binding.textcomdate.isVisible = true
      }
    }else {
      holder.binding.textcomdate.isVisible = true
    }*/

    if(details1RowModel.txtEmpName?.equals(details1RowModel.adminname) == true){
      holder.binding.layout1.isVisible = true
      holder.binding.layout2.isVisible = false
      if(details1RowModel.imgPath.isNullOrEmpty() || details1RowModel.imgPath.equals("")){
        holder.binding.imgMsgNotificationFrom.isVisible = false
      }else {
        holder.binding.imgMsgNotificationFrom.isVisible = true
      }
      holder.binding.textViewFrom.setText(details1RowModel.txtMessage)
      holder.binding.textdateFrom.setText(details1RowModel.txtDatetime)
    }else{
      holder.binding.layout1.isVisible = false
      holder.binding.layout2.isVisible = true
      if(details1RowModel.imgPath.isNullOrEmpty() || details1RowModel.imgPath.equals("")){
        holder.binding.imgMsgNotificationUser.isVisible = false
      }else {
        holder.binding.imgMsgNotificationUser.isVisible = true
      }
      holder.binding.textViewUser.setText(details1RowModel.txtMessage)
      holder.binding.textdateUser.setText(details1RowModel.txtDatetime)
      holder.binding.textnameUser.setText(details1RowModel.txtEmpName)
    }
  }

  override fun getItemCount(): Int = list.size
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<MessageReplyModel>) {
    list = newData
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  interface OnItemClickListener {
    fun onItemClick(
      view: View,
      position: Int,
      item: MessageReplyModel
    ) {
    }
  }

  inner class RowDetails1VH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowDetails1Binding = RowDetails1Binding.bind(itemView)
  }
}
