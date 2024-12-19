package uz.kmax.tarixtest.adapter

import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.MenuTestData
import uz.kmax.tarixtest.databinding.ItemTestMenuBinding
import java.util.Date

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.HistoryViewHolder>() {

    private var testData = ArrayList<MenuTestData>()
    private var onTestItemClickListener: ((testLocation: String, testCount: Int) -> Unit)? = null
    fun setOnTestItemClickListener(listener: (testLocation: String, testCount: Int) -> Unit) {
        onTestItemClickListener = listener
    }

    private var onTypeClickListener: ((type : Int,location : String) -> Unit)? = null
    fun setOnTypeClickListener(listener: (type : Int,location : String) -> Unit) {
        onTypeClickListener = listener
    }

    fun setData(data: ArrayList<MenuTestData>) {
        testData.clear()
        testData.addAll(data)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(var binding: ItemTestMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: MenuTestData) {
            when (data.testType) {
                0-> {
                    binding.testName.text = data.testName
                    binding.testCount.text = "Random"

                    val storage = Firebase.storage.getReference("TarixTest")
                    val imageRef: StorageReference =
                        storage.child("Test").child(data.testLocation)
                            .child("image.png")

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
                    if (data.testNewOld == 1) {
                        binding.testNewOld.visibility = View.VISIBLE
                    } else {
                        binding.testNewOld.visibility = View.INVISIBLE
                    }
                    binding.test.setOnClickListener {
                        onTestItemClickListener?.invoke(data.testLocation, data.testCount)
                    }
                }
                1-> {
                    val day : String = SimpleDateFormat("dd").format(Date())
                    val month : String = SimpleDateFormat("MM").format(Date())
                    val year : String= SimpleDateFormat("yyyy").format(Date())
                    binding.testName.text = data.testName
                    binding.testCount.text = "${day}.${month}.${year}"
                    binding.testNewOld.visibility = View.INVISIBLE

                    val storage = Firebase.storage.getReference("TarixTest")
                    val imageRef: StorageReference =
                        storage.child("DayHistory").child("image.png")

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
                    binding.test.setOnClickListener {
                        onTypeClickListener?.invoke(1,data.testLocation)
                    }
                }
                2-> {
                    val storage = Firebase.storage.getReference("TarixTest")
                    val imageRef: StorageReference =
                        storage.child("Message").child(data.testLocation)
                            .child("image.png")

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
                    binding.testName.text = data.testName
                    binding.testCount.text = "Message"
                    binding.testNewOld.visibility = View.INVISIBLE
                    binding.test.setOnClickListener {
                        onTypeClickListener?.invoke(2,data.testLocation)
                    }
                }
                3-> {
                    val storage = Firebase.storage.getReference("TarixTest")
                    val imageRef: StorageReference =
                        storage.child("Update").child("image.png")

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
                    binding.testName.text = data.testName
                    binding.testCount.text = (R.string.update).toString()
                    binding.testNewOld.visibility = View.INVISIBLE
                    binding.test.setOnClickListener {
                        onTypeClickListener?.invoke(3,data.testLocation)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryViewHolder(
            ItemTestMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) =
        holder.bindData(testData[position])

    override fun getItemCount() = testData.size
}