package uz.kmax.tarixtest.presentation.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import uz.kmax.tarixtest.databinding.DialogBookExistBinding

class DialogBookNotExist {
    private var downloadNow : ((type : Int)-> Unit)? = null
    fun setOnDownloadNowListener(f: (type : Int)-> Unit){ downloadNow = f }

    lateinit var binding: DialogBookExistBinding
    lateinit var dialog: Dialog
    private var type = 1

    fun show(context: Context,bookSize : String){
        dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        binding = DialogBookExistBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        binding.bookSizeText.text = "Kitob hajmi : $bookSize"

        binding.downloadBook.setOnClickListener {
            downloadNow?.invoke(type)
            binding.downloadLayout.visibility = View.VISIBLE
        }

        binding.dismissDownload.setOnClickListener {
            type = 1
            dialog.dismiss()
        }

        dialog.show()
    }

    fun downloadProgress(progress : Int){
        binding.progressDownload.progress = progress
        binding.progressPercentDownload.text = "$progress %"
    }

    fun setDownloadInfo(info : String){
        binding.downloadInfo.text = info
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

    fun setType(newType : Int,text : String){
        type = newType
        binding.downloadBook.text = text
    }
}