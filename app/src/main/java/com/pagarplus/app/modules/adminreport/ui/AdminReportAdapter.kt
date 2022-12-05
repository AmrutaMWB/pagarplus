package com.pagarplus.app.modules.adminreport.ui
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowAttendanceReportItemBinding
import com.pagarplus.app.databinding.RowSalaryReportItemBinding
import com.pagarplus.app.modules.adminreport.data.model.AdminReportRowModel

public class AdminReportAdapter(public var list: List<AdminReportRowModel>, val context:Context,var isSalaryWise:Boolean=false) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var clickListener: OnItemClickListener? = null
    public fun updateData(newData: List<AdminReportRowModel>): Unit {
        list = newData
        notifyDataSetChanged()
    }
    public fun setOnItemClickListener(clickListener: OnItemClickListener): Unit {
        this.clickListener = clickListener
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(isSalaryWise) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_salary_report_item, parent, false)
            return RowSalaryVH(view, clickListener!!)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_attendance_report_item, parent, false)
            return RowAttendanceVH(view, clickListener!!)
        }
    }

    public override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int): Unit {
        val reportRowModel = list[position]
        if(holder is RowAttendanceVH)
        holder.binding.attendanceReportModel = reportRowModel
        else if(holder is RowSalaryVH)
            holder.binding.salaryReportModel=reportRowModel
    }

    public override fun getItemCount(): Int = list.size

    public interface OnItemClickListener {
        public fun onItemClick(view: View, position: Int, item: AdminReportRowModel): Unit {
        }
    }

    public inner class RowAttendanceVH(view: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(view)
    {
        public val binding: RowAttendanceReportItemBinding = RowAttendanceReportItemBinding.bind(itemView)
        init {

            }
        }
public inner class RowSalaryVH(view: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(view)
{
    public val binding: RowSalaryReportItemBinding = RowSalaryReportItemBinding.bind(itemView)
    init {

    }
}

}


