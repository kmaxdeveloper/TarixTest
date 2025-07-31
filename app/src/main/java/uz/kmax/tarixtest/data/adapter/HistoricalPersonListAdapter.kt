package uz.kmax.tarixtest.data.adapter

import android.graphics.BitmapFactory
import android.view.View
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.tarixtest.domain.models.main.BaseBookData
import uz.kmax.tarixtest.databinding.ItemBookBinding
import uz.kmax.tarixtest.databinding.ItemHistoricalPersonBinding
import uz.kmax.tarixtest.domain.models.content.HistoricalPersonListData

class HistoricalPersonListAdapter :
    BaseRecycleViewDU<ItemHistoricalPersonBinding, HistoricalPersonListData>(ItemHistoricalPersonBinding::inflate) {

    override fun bind(binding: ItemHistoricalPersonBinding, item: HistoricalPersonListData) {
        binding.personName.text = item.personName
        binding.personYears.text = item.personYears
        setDataToView(binding, item.personLocation)
        if (item.personVisibility == 1) {
            binding.personNewOld.visibility = View.VISIBLE
        } else {
            binding.personNewOld.visibility = View.INVISIBLE
        }
        binding.bookLayout.setOnClickListener {
            sendData(item)
        }
    }

    private fun setDataToView(binding: ItemHistoricalPersonBinding, path: String) {
        val storage = Firebase.storage.getReference("TarixTest/Content/HistoricalPerson/$path")
        val imageRef: StorageReference =
            storage.child("image.png")

        imageRef.getBytes(1024 * 1024)
            .addOnSuccessListener { image ->
                binding.itemImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        image,
                        0,
                        image.size
                    )
                )
            }
    }

    override fun areContentsTheSame(oldItem: HistoricalPersonListData, newItem: HistoricalPersonListData) =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: HistoricalPersonListData, newItem: HistoricalPersonListData) =
        oldItem.personLocation == newItem.personLocation
}