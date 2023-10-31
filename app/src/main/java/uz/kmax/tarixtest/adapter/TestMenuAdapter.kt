package uz.kmax.tarixtest.adapter

import android.graphics.BitmapFactory
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

class TestMenuAdapter : RecyclerView.Adapter<TestMenuAdapter.HistoryViewHolder>() {

    private var testData = ArrayList<MenuTestData>()
    private var onTestItemClickListener: ((testLocation: String, testCount: Int) -> Unit)? = null
    fun setOnTestItemClickListener(listener: (testLocation: String, testCount: Int) -> Unit) {
        onTestItemClickListener = listener
    }

    private var onDayHistoryClickListener: (() -> Unit)? = null
    fun setOnDayHistoryClickListener(listener: () -> Unit) {
        onDayHistoryClickListener = listener
    }

    fun setData(data: ArrayList<MenuTestData>) {
        testData.clear()
        testData.addAll(data)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(var binding: ItemTestMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: MenuTestData) {
            if (data.testType != "Kun Tarixi") {
                binding.testType.text = data.testType
                binding.testCount.text = "${data.testCount} ta variant mavjud"
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
            } else {
                binding.testType.text = data.testType
                binding.testCount.text = ""
                binding.testNewOld.visibility = View.INVISIBLE
                binding.itemImage.setImageResource(R.drawable.calendar_24px)
                binding.test.setOnClickListener {
                    onDayHistoryClickListener?.invoke()
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

//    val Int.toDp: Int
//        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
//    val Int.toPx: Int
//        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}