package com.pagarplus.app.modules.expensereport.ui
import android.content.Context
import android.graphics.Color
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pagarplus.app.R
import com.pagarplus.app.databinding.RowExpenseReportBinding
import com.pagarplus.app.databinding.RowExpenseratingItemBinding
import com.pagarplus.app.modules.expensereport.data.model.ExpenseRatingItem
import kotlin.Int
import kotlin.Unit

public class ExpenseRatingAdapter(public var list: List<ExpenseRatingItem>, val context:Context) : RecyclerView.Adapter<ExpenseRatingAdapter.RowRatingVH>(){
    private var clickListener: OnItemClickListener? = null
    var colorList= arrayListOf<String>("#f57c00","#61A3CF","#107FC9","#B19893","#00fe77","#61A3CF")
    public fun updateData(newData: List<ExpenseRatingItem>): Unit {
        list = newData
        notifyDataSetChanged()
    }
    public fun setOnItemClickListener(clickListener: OnItemClickListener): Unit {
        this.clickListener = clickListener
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowRatingVH {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_expenserating_item,parent,false)
        return RowRatingVH(view,clickListener!!)
    }

    public override fun onBindViewHolder(holder: RowRatingVH, position: Int): Unit {
        val ratingRowModel = list[position]
        val progressColor = arrayOf<Pair<*, *>>(Pair(Color.parseColor("#FFFFFF"), Color.parseColor(colorList[position])))
        holder.binding.expenseRating = ratingRowModel
        holder.binding.progressbar.createRatingBars(100, arrayOf("null"),progressColor, intArrayOf(ratingRowModel.ExpensePercentageVal))
    }

    public override fun getItemCount(): Int = list.size

    public interface OnItemClickListener {
        public fun onItemClick(view: View, position: Int, item: ExpenseRatingItem): Unit {
        }
    }

    public inner class RowRatingVH(view: View,listener:OnItemClickListener) : RecyclerView.ViewHolder(view)
    {
        public val binding: RowExpenseratingItemBinding = RowExpenseratingItemBinding.bind(itemView)
        init {
            binding.ratingCard.setOnClickListener {
                listener.onItemClick(it,adapterPosition,list[adapterPosition])
            }
            binding.linearRating.setOnClickListener {
                listener.onItemClick(it,adapterPosition,list[adapterPosition])
            }
        }

    }

}
