package uz.kmax.tarixtest.presentation.ui.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.tarixtest.databinding.DialogUpdateBinding

class DialogUpdate {

    private var updateNowClickListener: (() -> Unit)? = null
    fun setOnUpdateNowListener(f: () -> Unit) {
        updateNowClickListener = f
    }

    private var exitClickListener: (() -> Unit)? = null
    fun setOnExitListener(f: () -> Unit) {
        exitClickListener = f
    }

    fun show(context: Context){
        val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogUpdateBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.dialogUpdateBtnExit.setOnClickListener {
            exitClickListener?.invoke()
            dialog.dismiss()
        }

        binding.dialogUpdateBtnUpdateNow.setOnClickListener {
            updateNowClickListener?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }
}