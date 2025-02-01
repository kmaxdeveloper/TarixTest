package uz.kmax.tarixtest.tools.tools

import androidx.recyclerview.widget.DiffUtil

class BaseDiffUtilCallback<D>(
    private val oldList: List<D>,
    private val newList: List<D>,
    private val areItemsTheSame: (D, D) -> Boolean,
    private val areContentsTheSame: (D, D) -> Boolean
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }
}
