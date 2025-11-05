package uz.kmax.tarixtest.presentation.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random
import androidx.core.graphics.scale
import uz.kmax.tarixtest.R

class SnowView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val snowBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.image_snowflake)

    private val snowflakes = mutableListOf<Snowflake>()

    init {
        for (i in 0 until 60) {
            snowflakes.add(Snowflake())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (flake in snowflakes) {
            flake.y += flake.speed

            // Pastga tushib ketganda tepaga qaytar
            if (flake.y > height) {
                flake.y = -flake.size
                flake.x = Random.nextFloat() * width
            }

            val size = flake.size.toInt()
            val scaled = snowBitmap.scale(size, size, false)
            canvas.drawBitmap(scaled, flake.x, flake.y, null)
        }

        postInvalidateDelayed(16)
    }

    inner class Snowflake {
        var x = Random.nextFloat() * 1080f
        var y = Random.nextFloat() * 1920f
        var size = Random.nextFloat() * 64f + 16f // donachaning o‘lchami (px)
        var speed = Random.nextFloat() * 3f + 1f
    }
}
