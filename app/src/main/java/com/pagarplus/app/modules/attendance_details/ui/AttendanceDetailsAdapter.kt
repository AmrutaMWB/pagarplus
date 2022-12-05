package com.pagarplus.app.modules.attendance_details.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowAttendanceDetailsBinding
import com.pagarplus.app.databinding.RowAttendancelistBinding
import com.pagarplus.app.modules.adminattendancelist.`data`.model.AttendanceRowModel
import com.pagarplus.app.modules.aprrejloanleavelist.data.model.MessageListModel
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import kotlin.Int
import kotlin.collections.List

class AttendanceDetailsAdapter(
  var list: List<AttendanceDetailsModel>
) : RecyclerView.Adapter<AttendanceDetailsAdapter.RowAttendanceDetailsVH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowAttendanceDetailsVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_attendance_details,parent,false)
    return RowAttendanceDetailsVH(view)
  }

  override fun onBindViewHolder(holder: RowAttendanceDetailsVH, position: Int) {
    val attendanceDetailsModel = list[position]
    holder.binding.attendanceDetailsModel = attendanceDetailsModel
    if(position % 2==0)
      holder.binding.linearRowname.setBackgroundResource(R.color.gray_200)
    else
      holder.binding.linearRowname.setBackgroundResource(R.color.white)

    if(!attendanceDetailsModel.txtStatus.isNullOrEmpty()){
      if(attendanceDetailsModel.txtStatus.equals("Approved")){
        holder.binding.txtStatus.setBackgroundResource(R.color.green_A700)
      }else if(attendanceDetailsModel.txtStatus.equals("Rejected")) {
        holder.binding.txtStatus.setBackgroundResource(R.color.red_600)
      }else{
        holder.binding.txtStatus.setBackgroundResource(R.color.white)
      }
    }else{
      attendanceDetailsModel.txtStatus = "Pending"
    }

    if(attendanceDetailsModel.txtbranch.isNullOrEmpty()){
      attendanceDetailsModel.txtbranch = attendanceDetailsModel.organizationname
    }
  }

  override fun getItemCount(): Int = list.size

  public fun updateData(newData: List<AttendanceDetailsModel>) {
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
      item: AttendanceDetailsModel
    ) {
    }
  }

  inner class RowAttendanceDetailsVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowAttendanceDetailsBinding = RowAttendanceDetailsBinding.bind(itemView)
    init {
      binding.linearRowname.setOnClickListener {
        AttendanceDialog(binding.root.context,adapterPosition,list[adapterPosition]).show()
      }
    }
  }
}
