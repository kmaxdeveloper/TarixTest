package uz.kmax.tarixtest.data.adapter

import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.tarixtest.domain.models.main.DayHistoryData
import uz.kmax.tarixtest.databinding.ItemDayHistoryBinding

class DayHistoryAdapter : BaseRecycleViewDU<ItemDayHistoryBinding, DayHistoryData>(ItemDayHistoryBinding::inflate) {

    override fun bind(binding: ItemDayHistoryBinding, item: DayHistoryData) {
        binding.storyOfDay.text = item.story
    }

    override fun areContentsTheSame(oldItem: DayHistoryData, newItem: DayHistoryData) = oldItem == newItem

    override fun areItemsTheSame(oldItem: DayHistoryData, newItem: DayHistoryData) = oldItem.story == newItem.story

}