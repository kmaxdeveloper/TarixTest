package uz.kmax.tarixtest.data.adapter

import android.graphics.BitmapFactory
import android.view.View
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.domain.models.main.MenuTestData
import uz.kmax.tarixtest.databinding.ItemTestMenuBinding

class TestListAdapter() : BaseRecycleViewDU<ItemTestMenuBinding, MenuTestData>(ItemTestMenuBinding::inflate) {
    private lateinit var firebaseManager : FirebaseManager

    override fun bind(binding: ItemTestMenuBinding, item: MenuTestData) {
        firebaseManager = FirebaseManager()
        binding.testName.text = item.testName
        binding.testCount.text = "Random"
        if (item.testNewOld == 1) {
            binding.testNewOld.visibility = View.VISIBLE
        } else {
            binding.testNewOld.visibility = View.INVISIBLE
        }

        binding.test.setOnClickListener {
            firebaseManager.getChildCount("Test/uz/${item.testLocation}"){
                sendMessage(it.toInt(),item.testLocation)
                sendData(item)
            }
        }

        val storage = Firebase.storage.getReference("TarixTest")
        val imageRef: StorageReference =
            storage.child("Test/${item.testLocation}").child("image.png")

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

    override fun areContentsTheSame(oldItem: MenuTestData, newItem: MenuTestData) = oldItem == newItem

    override fun areItemsTheSame(oldItem: MenuTestData, newItem: MenuTestData) = oldItem.testName == newItem.testName
}