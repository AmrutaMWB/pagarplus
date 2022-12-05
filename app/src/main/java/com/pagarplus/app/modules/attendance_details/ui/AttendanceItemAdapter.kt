package com.pagarplus.app.modules.attendance_details.ui


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.appcomponents.di.MyApp
import com.pagarplus.app.databinding.RowUserAttendanceReportBinding
import com.pagarplus.app.extensions.PagarStatus
import com.pagarplus.app.extensions.extractDate
import com.pagarplus.app.network.models.AdminDashboard.AttendanceItem
import com.pagarplus.app.network.models.AdminDashboard.LeaveDetailItem


class AttendanceItemAdapter(var list: List<AttendanceItem>
) : RecyclerView.Adapter<AttendanceItemAdapter.RowAttItemVH>() {
    private var clickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowAttItemVH {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_user_attendance_report,parent,false)
        return RowAttItemVH(view)
    }
    override fun onBindViewHolder(holder: RowAttItemVH, position: Int) {
        val attendanceDetailsModel = list[position]
        holder.binding.txtDate.text=attendanceDetailsModel.Date?.extractDate()
        if(attendanceDetailsModel.IsHoliday == true){
            holder.binding.AttHeader.setBackgroundResource(R.color.yellow_A400)
            holder.binding.AttHeader.setTextColor(Color.BLACK)
            holder.binding.btnShowMore.visibility=View.GONE
            holder.binding.recyclerPresenceList.visibility=View.GONE
            if(attendanceDetailsModel.HolidayDetails?.get(0)?.IsHalfDay == true) {
                holder.binding.AttHeader.text= MyApp.getInstance().resources.getString(R.string.lbl_halfDay)
            } else
            holder.binding.AttHeader.text= attendanceDetailsModel.HolidayDetails?.get(0)?.HolidayType?:MyApp.getInstance().resources.getString(R.string.lbl_holiday)

            holder.binding.txtDetails.text=attendanceDetailsModel.HolidayDetails?.get(0)?.ReasonForHoliday
        }else if(attendanceDetailsModel.IsLeaveApplied==true){
            var leaveDetails=attendanceDetailsModel.LeaveDetails?.get(0)?: LeaveDetailItem()

            holder.binding.txtDetails.visibility=View.VISIBLE
            holder.binding.btnShowMore.visibility=View.GONE
            holder.binding.AttHeader.setTextColor(Color.WHITE)
            holder.binding.recyclerPresenceList.visibility=View.GONE
            if(leaveDetails.LeaveStatus?.equals(PagarStatus.Rejected,true) == true) {
                holder.binding.AttHeader.text = MyApp.getInstance().resources.getString(R.string.lbl_absent_wcol)
                holder.binding.AttHeader.setBackgroundResource(R.color.red_600)
            }
            else {
                holder.binding.AttHeader.text = MyApp.getInstance().resources.getString(R.string.lbl_leave)
                holder.binding.AttHeader.setBackgroundResource(R.color.red_600_96)
            }
            holder.binding.txtDetails.text=leaveDetails.LeaveStatus
        }else{

            if(attendanceDetailsModel.Attendance.isNullOrEmpty()){
                holder.binding.AttHeader.setBackgroundResource(R.color.red_600)
                holder.binding.AttHeader.text= MyApp.getInstance().resources.getString(R.string.lbl_absent_wcol)
                holder.binding.txtDetails.text=MyApp.getInstance().resources.getString(R.string.lbl_no_data)
                holder.binding.AttHeader.setTextColor(Color.WHITE)
                holder.binding.btnShowMore.visibility=View.GONE
                holder.binding.recyclerPresenceList.visibility=View.GONE
            }
            else{
                holder.binding.AttHeader.setBackgroundResource(R.color.green_A700)
                holder.binding.AttHeader.text= MyApp.getInstance().resources.getString(R.string.lbl_work_days)
                holder.binding.txtDetails.visibility=View.GONE
                holder.binding.btnShowMore.visibility=View.VISIBLE
                holder.binding.AttHeader.setTextColor(Color.BLACK)
                holder.binding.txtDetails.text=""
                var adapter= attendanceDetailsModel.AttenDanceDetailList?.let { AttendanceDetailsAdapter(it) }
                holder.binding.recyclerPresenceList.adapter=adapter
                adapter?.notifyDataSetChanged()

            }
        }
    }

    override fun getItemCount(): Int = list.size

    public fun updateData(newData: List<AttendanceItem>) {
        list = newData
        notifyDataSetChanged()

    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, item: AttendanceItem) {
        }
    }

    inner class RowAttItemVH(view: View) : RecyclerView.ViewHolder(view) {
        val binding: RowUserAttendanceReportBinding = RowUserAttendanceReportBinding.bind(itemView)
        var slideUp: Animation = AnimationUtils.loadAnimation(itemView.context, com.pagarplus.app.R.anim.slide_up)
        var slidown: Animation = AnimationUtils.loadAnimation(itemView.context, com.pagarplus.app.R.anim.slide_down)
        init {

            binding.btnShowMore.setOnClickListener {
                var recyclerview=binding.recyclerPresenceList
                var infoCard=binding.infoCard
             if(recyclerview.isVisible) {
                 TransitionManager.beginDelayedTransition(infoCard,AutoTransition())
                recyclerview.visibility = View.GONE
                 binding.btnShowMore.setImageResource(R.drawable.ic_arrow_down)
               //  viewGoneAnimator(recyclerview)
                // recyclerview.startAnimation(slidown)
             }
                else {
                 TransitionManager.beginDelayedTransition(infoCard,AutoTransition())
                  recyclerview.visibility = View.VISIBLE
                 binding.btnShowMore.setImageResource(R.drawable.ic_arrow_up)

                 // recyclerview.startAnimation(slideUp)
                  //  viewVisibleAnimator(recyclerview)

             }
            }
            binding.constraintGroup.setOnClickListener {
                if(list[adapterPosition].IsLeaveApplied==true) {
                    var item = list[adapterPosition].LeaveDetails?.get(0)
                    val builder = AlertDialog.Builder(it.context)
                    builder.setIcon(android.R.drawable.ic_dialog_info)
                    builder.setTitle("Leave Details")
                    val strBuilder = StringBuilder()
                    strBuilder.appendLine("Status: ${item?.LeaveStatus}")
                    strBuilder.appendLine("LeaveType: ${item?.LeaveType}")
                    strBuilder.appendLine("Reason For Leave: ${item?.ReasonForLeave}")
                    builder.setMessage(strBuilder)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(true)
                    alertDialog.show()
                }
            }

        }

        private fun viewGoneAnimator(view: View) {
            view.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                    }
                })
        }

        private fun viewVisibleAnimator(view: View) {
            view.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.VISIBLE
                    }
                })
        }

    }

}
