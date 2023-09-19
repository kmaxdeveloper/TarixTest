package uz.kmax.tarixtest.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.tarixtest.databinding.DialogEndTestBinding

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