package uz.kmax.tarixtest.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.widget.Toast
import uz.kmax.tarixtest.databinding.DialogDataPickerBinding
import java.util.Date

class DialogDatePicker {

    private var dateClickListener: ((month: Int,day : Int,year : Int) -> Unit)? = null
    fun setOnDateListener(f: (month: Int,day : Int,year : Int) -> Unit) {
        dateClickListener = f
    }

    fun show(context: Context){

        val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogDataPickerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        var setDay = 1
        var setMonth = 1
        var setYear = 1970

        val today = Calendar.getInstance()
        binding.datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            val month2 = month + 1
            setMonth = month2
            setDay = day
            setYear = year
            val msg = "You Selected: $day/$month2/$year"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        binding.ok.setOnClickListener {
            dateClickListener?.invoke(setMonth,setDay,setYear)
            dialog.dismiss()
        }

        binding.return2.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}