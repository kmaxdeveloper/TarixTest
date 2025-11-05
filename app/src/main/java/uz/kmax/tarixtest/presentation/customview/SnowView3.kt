package uz.kmax.tarixtest.presentation.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.scale
import uz.kmax.tarixtest.R
import kotlin.random.Random

class SnowView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // 🔹 Qor rasmi ro'yxati
    private var snowBitmaps: List<Bitmap> = listOf(
        BitmapFactory.decodeResource(resources, R.drawable.image_snowflake3)
    )

    private val snowflakes = mutableListOf<Snowflake>()

    // 🔹 Animatsiya holatini nazorat qilish flagi
    private var isSnowing = true

    init {
        for (i in 0 until 60) {
            snowflakes.add(Snowflake())
        }
        initSnowflakes()
    }

    // 🔹 Qo‘lda qor rasmi o‘rnatish (3 tagacha)
    fun setSnowflakeImages(vararg resIds: Int) {
        if (resIds.isNotEmpty()) {
            snowBitmaps = resIds.map { id ->
                BitmapFactory.decodeResource(resources, id)
            }
        } else {
            snowBitmaps = listOf(
                BitmapFactory.decodeResource(resources, R.drawable.image_snowflake3)
            )
        }

        initSnowflakes()
        invalidate()
    }

    private fun initSnowflakes() {
        snowflakes.forEach { flake ->
            val randomBitmap = snowBitmaps.random()
            flake.scaledBitmap = flake.createScaledBitmap(randomBitmap)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 🔹 Agar animatsiya o‘chirilgan bo‘lsa, faqat rasmni chizadi, lekin harakat yo‘q
        for (flake in snowflakes) {
            if (isSnowing) {
                flake.y += flake.speed

                if (flake.y > height) {
                    flake.y = -flake.size
                    flake.x = Random.nextFloat() * width
                }
            }

            flake.scaledBitmap?.let { scaled ->
                canvas.drawBitmap(scaled, flake.x, flake.y, null)
            }
        }

        // 🔹 Faqat animatsiya yoqilganida qayta chizishni davom ettiramiz
        if (isSnowing) {
            postInvalidateDelayed(16)
        }
    }

    // 🔹 Animatsiyani yoqish
    fun startSnow() {
        if (!isSnowing) {
            isSnowing = true
            postInvalidateOnAnimation() // qayta chizishni boshlaydi
        }
    }

    // 🔹 Animatsiyani to‘xtatish
    fun stopSnow() {
        isSnowing = false
        invalidate() // hozirgi kadrni oxirgi holatda chizadi
    }

    inner class Snowflake {
        var x = Random.nextFloat() * 1000f
        var y = Random.nextFloat() * 2000f
        var size = Random.nextFloat() * 64f + 16f
        var speed = Random.nextFloat() * 3f + 1f
        var scaledBitmap: Bitmap? = null

        fun createScaledBitmap(originalBitmap: Bitmap): Bitmap {
            val sizeInt = size.toInt()
            return originalBitmap.scale(sizeInt, sizeInt, false)
        }
    }
}
