package uz.kmax.tarixtest.presentation.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.snackbar.Snackbar
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.databinding.DialogBackBinding
import uz.kmax.tarixtest.databinding.DialogSelectTestTypeBinding

class DialogTestTypeSelection {
    private var onTypeClickListener : ((type : Int)-> Unit)? = null
    fun setOnTypeListener(f: (type : Int)-> Unit){ onTypeClickListener = f }

    private var onDismissClickListener : (()-> Unit)? = null
    fun setOnDismissListener(f: ()-> Unit){ onDismissClickListener = f }

    fun show(context : Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogSelectTestTypeBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        binding.infinityHeartBtn.setOnClickListener {
            binding.testTypeInfinityHeartSelected.visibility = View.VISIBLE
            binding.testTypeHeartSelected.visibility = View.GONE
            binding.chooseTestTypeBtn.visibility = View.VISIBLE
            onTypeClickListener?.invoke(1)
        }

        binding.costHeartBtn.setOnClickListener {
            binding.testTypeHeartSelected.visibility = View.VISIBLE
            binding.testTypeInfinityHeartSelected.visibility = View.GONE
            binding.chooseTestTypeBtn.visibility = View.VISIBLE
            onTypeClickListener?.invoke(3)
        }

        binding.chooseTestTypeBtn.setOnClickListener {
            dialog.dismiss()
            onDismissClickListener?.invoke()
        }
    }
}