package uz.kmax.tarixtest.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import uz.kmax.tarixtest.databinding.DialogEndTestBinding
import java.util.concurrent.TimeUnit

class DialogEndTest {

    private var okBtnClickListener: (() -> Unit)? = null
    fun setOnOkBtnListener(f: () -> Unit) {
        okBtnClickListener = f
    }

    private var reStartClickListener: (() -> Unit)? = null
    fun setOnReStartListener(f: () -> Unit) {
        reStartClickListener = f
    }

    fun show(context: Context, correctAnswerCount: Int, wrongAnswerCount: Int) {
        val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogEndTestBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        var partyy = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            timeToLive = 1000L,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 6, TimeUnit.MILLISECONDS).max(200).perSecond(300)
        )

        val party = Party(
            colors = listOf(Color.YELLOW, Color.GREEN, Color.BLUE),
            angle = 0,
            spread = 360,
            speed = 1f,
            maxSpeed = 10f,
            fadeOutEnabled = true,
            timeToLive = 5000L,
            shapes = listOf(Shape.Square, Shape.Circle),
            size = listOf(Size(12)),
            position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
            emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(300)
        )
        binding.viewKonfetti.start(party)

        binding.correctAnswerCount.text = correctAnswerCount.toString()
        binding.wrongAnswerCount.text = wrongAnswerCount.toString()

        binding.okBtn.setOnClickListener {
            dialog.dismiss()
            okBtnClickListener?.invoke()
        }

        binding.reStart.setOnClickListener {
            dialog.dismiss()
            reStartClickListener?.invoke()
        }
        dialog.show()
    }
}