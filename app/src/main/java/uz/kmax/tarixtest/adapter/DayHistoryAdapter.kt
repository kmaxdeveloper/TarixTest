package uz.kmax.tarixtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.kmax.tarixtest.data.DayHistoryData
import uz.kmax.tarixtest.databinding.ItemDayOfHistoryBinding

class DayHistoryAdapter : RecyclerView.Adapter<DayHistoryAdapter.HistoryViewHolder>() {

    private var dayHistoryData = ArrayList<DayHistoryData>()

    fun setData(data: ArrayList<DayHistoryData>) {
        dayHistoryData.clear()
        dayHistoryData.addAll(data)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(var binding: ItemDayOfHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: DayHistoryData) {
            binding.storyOfDay.text = data.story
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryViewHolder(
            ItemDayOfHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) =
        holder.bindData(dayHistoryData[position])

    override fun getItemCount() = dayHistoryData.size
}