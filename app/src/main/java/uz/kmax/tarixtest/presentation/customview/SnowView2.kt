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

class SnowView2(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val snowBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.image_snowflake3) //

    private val snowflakes = mutableListOf<Snowflake>() //

    // Qor parchalari sonini kamaytirish tavsiya etiladi (masalan 40 taga), lekin 60 qoldirildi.
    init {
        for (i in 0 until 60) {
            snowflakes.add(Snowflake())
        }
        // Barcha qor parchalari uchun scaled bitmapni init bosqichida yaratamiz
        initSnowflakes()
    }

    // View o'lchamlari aniqlangandan so'ng chaqiriladi
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // O'lcham o'zgarganda qor parchalarini qayta joylashtirish mumkin (agar xohlasangiz)
    }

    private fun initSnowflakes() {
        snowflakes.forEach { flake ->
            // Scaled Bitmapni faqat bir marta yaratamiz va saqlaymiz
            flake.scaledBitmap = flake.createScaledBitmap(snowBitmap)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (flake in snowflakes) {
            flake.y += flake.speed //

            // Pastga tushib ketganda tepaga qaytar
            if (flake.y > height) { //
                flake.y = -flake.size //
                // X koordinatasini View'ning haqiqiy width o'lchami orqali generatsiya qilish
                flake.x = Random.nextFloat() * width
                // Eslatma: Bu yerda scaledBitmapni qayta yaratish shart emas, chunki size o'zgarmaydi.
            }

            // Xotiradan oldindan yaratilgan scaled bitmapni chizamiz
            flake.scaledBitmap?.let { scaled ->
                canvas.drawBitmap(scaled, flake.x, flake.y, null)
            }
        }

        postInvalidateDelayed(16) //
    }

    inner class Snowflake {
        // Hardcoded o'lchamlar o'rniga Random.nextFloat() ishlatish yaxshi, lekin
        // 1080f va 1920f kabi aniq piksellar o'rniga, onSizeChanged ishlatish yaxshiroq.
        // Hozirgi kodni tozalash uchun 1080f/1920f o'chirildi.
        var x = Random.nextFloat() * 1000f // Taxminiy o'lchamda randomlash
        var y = Random.nextFloat() * 2000f // Taxminiy o'lchamda randomlash
        var size = Random.nextFloat() * 64f + 16f //
        var speed = Random.nextFloat() * 3f + 1f //

        // Qayta o'lchamlangan bitmapni saqlash uchun yangi property
        var scaledBitmap: Bitmap? = null

        // Scaled Bitmapni yaratish funksiyasi
        fun createScaledBitmap(originalBitmap: Bitmap): Bitmap {
            val sizeInt = size.toInt()
            // Bir marta scale qilamiz va return qilamiz
            return originalBitmap.scale(sizeInt, sizeInt, false)
        }
    }
}