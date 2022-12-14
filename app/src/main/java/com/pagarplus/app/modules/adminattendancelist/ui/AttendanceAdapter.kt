package com.pagarplus.app.modules.adminattendancelist.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.utility.PreferenceHelper
import com.pagarplus.app.databinding.RowAttendancelistBinding
import com.pagarplus.app.modules.adminattendancelist.`data`.model.AttendanceRowModel
import com.pagarplus.app.modules.adminemplist.data.model.DetailsRowModel
import com.pagarplus.app.modules.attendance_details.data.model.AttendanceDetailsModel
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.inject
import kotlin.Int
import kotlin.collections.List

class AttendanceAdapter(
  var list: List<AttendanceRowModel>
) : RecyclerView.Adapter<AttendanceAdapter.RowAttendanceVH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowAttendanceVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_attendancelist,parent,false)
    return RowAttendanceVH(view)
  }

  override fun onBindViewHolder(holder: RowAttendanceVH, position: Int) {
    val attendanceRowModel = list[position]
    holder.binding.attendanceRowModel = attendanceRowModel

    /*if(attendanceRowModel.txtStatus.isNullOrEmpty() || attendanceRowModel.txtStatus.equals(" ")){
      if(attendanceRowModel.txtType.equals("Absent")) {
        attendanceRowModel.txtStatus = ""
      }else{
        attendanceRowModel.txtStatus = "Pending"
      }
    }else{
      if(attendanceRowModel.txtStatus.equals("Approved")){
        holder.binding.txtStatus.setTextColor(Color.parseColor("#24BC0C"))
      }else if(attendanceRowModel.txtStatus.equals("Rejected")) {
        holder.binding.txtStatus.setTextColor(Color.parseColor("#d95538"))
      }else{
        holder.binding.txtStatus.setTextColor(Color.parseColor("#ffffff"))
      }
    }*/
  }

  override fun getItemCount(): Int = list.size
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<AttendanceRowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  // method for filtering our recyclerview items.
  fun filterList(filterlist: ArrayList<AttendanceRowModel>) {
    list = filterlist
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  interface OnItemClickListener {
    fun onItemClick(
      view: View,
      position: Int,
      item: AttendanceRowModel
    ) {
    }
  }

  inner class RowAttendanceVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowAttendancelistBinding = RowAttendancelistBinding.bind(itemView)
    init {
      binding.linearAttendanceRow.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, AttendanceRowModel())
      }
      binding.txtEmployee.setOnClickListener {
        clickListener?.onItemClick(it, adapterPosition, AttendanceRowModel())
      }
    }
  }
}
