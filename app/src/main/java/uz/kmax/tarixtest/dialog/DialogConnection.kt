package uz.kmax.tarixtest.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.tarixtest.databinding.DialogCheckConnectionBinding

class DialogConnection {

    private var tryAgainClickListener : (()-> Unit)? = null
    fun setOnTryAgainListener(f: ()-> Unit){ tryAgainClickListener = f }

    private var closeClickListener : (()-> Unit)? = null
    fun setOnCloseListener(f: ()-> Unit){ closeClickListener = f }

    fun show(context: Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogCheckConnectionBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.tryAgain.setOnClickListener {
            dialog.dismiss()
            tryAgainClickListener?.invoke()
        }

        binding.close.setOnClickListener {
            dialog.dismiss()
            closeClickListener?.invoke()
        }
        dialog.show()
    }
}