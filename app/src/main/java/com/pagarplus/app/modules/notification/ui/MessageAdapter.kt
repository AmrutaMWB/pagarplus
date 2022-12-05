package com.pagarplus.app.modules.notification.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowMessageBinding
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.modules.notification.data.model.MessageRowModel
import com.pagarplus.app.modules.notificationcreatemessage.ui.NotificationCreateMessageActivity
import com.pagarplus.app.modules.replymessage.ui.ReplyActivity
import kotlin.Int
import kotlin.collections.List

class MessageAdapter(
  var list: List<MessageRowModel>
) : RecyclerView.Adapter<MessageAdapter.RowMessageVH>() {
  private var clickListener: OnItemClickListener? = null

  public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowMessageVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_message,parent,false)
    return RowMessageVH(view)
  }

  public override fun onBindViewHolder(holder: RowMessageVH, position: Int) {
    val messageRowModel = list[position]
    holder.binding.messageRowModel = messageRowModel

    if(messageRowModel.txtBranch.isNullOrEmpty() || messageRowModel.txtBranch.equals(" ")){
      messageRowModel.txtBranch = messageRowModel.organizationName
    }else{

    }

    if(messageRowModel.txtDept.isNullOrEmpty()){
      holder.binding.txtDept.isVisible = false
    }else{
      holder.binding.txtDept.isVisible = true
    }

    if(messageRowModel.txtImgpath.isNullOrEmpty() || messageRowModel.txtImgpath.equals(" ")){
      holder.binding.imageNoti.setImageResource(R.drawable.image_not_found)
    }
  }

  public override fun getItemCount(): Int = list.size
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<MessageRowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  // method for filtering our recyclerview items.
  fun filterList(filterlist: ArrayList<MessageRowModel>) {
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
      item: MessageRowModel
    ) {

    }
  }

  inner class RowMessageVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowMessageBinding = RowMessageBinding.bind(itemView)
    init {
      binding.linearTextRow.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, MessageRowModel())
      }
      binding.imageNoti.setOnClickListener {
        // TODO replace with value from datasource
        clickListener?.onItemClick(it, adapterPosition, MessageRowModel())
      }
    }
  }
}
