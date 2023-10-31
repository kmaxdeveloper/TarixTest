package uz.kmax.tarixtest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import uz.kmax.tarixtest.R

class TestViewPagerAdapter(val ctx: Context) : RecyclerView.Adapter<TestViewPagerAdapter.ViewHolder>() {
    private val images = intArrayOf(
        R.raw.hello_animation1,
        R.raw.world_map1,
        R.raw.search_in_book2,
        R.raw.test_collection3,
        R.raw.research_time4,
        R.raw.checking_iq5
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.item_viewpager2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.animation.setAnimation(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var animation: LottieAnimationView

        init {
            animation = itemView.findViewById(R.id.lottieAnimation)
        }
    }
}