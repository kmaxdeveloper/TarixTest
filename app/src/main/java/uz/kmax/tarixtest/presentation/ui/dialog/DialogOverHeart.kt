package uz.kmax.tarixtest.presentation.ui.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.tarixtest.databinding.DialogOverHeartBinding

class DialogOverHeart {

    private var exitBtnClickListener: (() -> Unit)? = null
    fun setOnExitBtnListener(f: () -> Unit) {
        exitBtnClickListener = f
    }

    private var onViewAdClickListener: (() -> Unit)? = null
    fun setOnViewAdListener(f: () -> Unit) {
        onViewAdClickListener = f
    }

    fun show(context : Context){
        val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogOverHeartBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.watchBtn.setOnClickListener {
            onViewAdClickListener?.invoke()
            dialog.dismiss()
        }

        binding.exit.setOnClickListener {
            dialog.dismiss()
            exitBtnClickListener?.invoke()
        }

        dialog.show()
    }
}