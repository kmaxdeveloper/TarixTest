package uz.kmax.tarixtest.adapter

import android.graphics.BitmapFactory
import android.view.View
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.tarixtest.data.main.MenuTestData
import uz.kmax.tarixtest.databinding.ItemTestMenuBinding
import uz.kmax.tarixtest.tools.firebase.FirebaseManager

class TestListAdapter() : BaseRecycleViewDU<ItemTestMenuBinding, MenuTestData>(ItemTestMenuBinding::inflate) {

    lateinit var firebaseManager : FirebaseManager

    override fun bind(binding: ItemTestMenuBinding, item: MenuTestData) {
        firebaseManager = FirebaseManager("TarixTest")
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