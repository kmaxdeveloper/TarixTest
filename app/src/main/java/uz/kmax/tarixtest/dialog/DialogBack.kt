package uz.kmax.tarixtest.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.tarixtest.databinding.DialogBackBinding

class DialogBack {

    private var backYesClickListener : (()-> Unit)? = null
    fun setOnBackYesListener(f: ()-> Unit){ backYesClickListener = f }

    fun show(context: Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogBackBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.dialogBackYes.setOnClickListener {
            dialog.dismiss()
            backYesClickListener?.invoke()
        }

        binding.dialogBackNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}