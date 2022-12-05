package com.pagarplus.app.modules.aprrejloanleavelist.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowAprrejlevlonlistBinding
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.MessageListModel
import kotlin.Int
import kotlin.collections.List

class LeaveListAdapter(
    var list: List<MessageListModel>
) : RecyclerView.Adapter<LeaveListAdapter.RowList1VH>() {
    private var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowList1VH {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_aprrejlevlonlist,parent,false)
        return RowList1VH(view)
    }

    override fun onBindViewHolder(holder: RowList1VH, position: Int) {
        val messageListModel = list[position]
        holder.binding.messageListModel = messageListModel
        if(list.get(position).txtStatus.equals("Approved")){
            holder.binding.txtStatus.setTextColor(Color.parseColor("#24BC0C"))
        }else if(list.get(position).txtStatus.equals("Rejected")){
            holder.binding.txtStatus.setTextColor(Color.parseColor("#d95538"))
        }else {
            holder.binding.txtStatus.setTextColor(Color.parseColor("#FF000000"))
        }

        if(messageListModel.txtBranch.isNullOrEmpty() || messageListModel.txtBranch.equals(" ")){
            messageListModel.txtBranch = messageListModel.organizationname
        }else{

        }

        if(messageListModel.txtDept.isNullOrEmpty()){
            holder.binding.txtDept.isVisible = false
        }else{
            holder.binding.txtDept.isVisible = true
        }

        if(messageListModel.txtLeaveType.isNullOrEmpty()){
            holder.binding.txtloanordateHead.setText("Requested Amount : Rs.")
        }else{
            holder.binding.txtloanordateHead.setText("Leave Date : ")
        }
    }

    override fun getItemCount(): Int = list.size
    // TODO uncomment following line after integration with data source
    // return list.size

    public fun updateData(newData: List<MessageListModel>) {
        list = newData
        notifyDataSetChanged()
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<MessageListModel>) {
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
            item: MessageListModel
        ) {
        }
    }

    inner class RowList1VH(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val binding: RowAprrejlevlonlistBinding = RowAprrejlevlonlistBinding.bind(itemView)
        init {
            binding.linearlistapprovedrejected.setOnClickListener {
                clickListener?.onItemClick(it, adapterPosition, MessageListModel())
            }
        }
    }
}
