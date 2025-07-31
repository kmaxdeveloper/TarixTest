package uz.kmax.tarixtest.presentation.ui.dialog

import android.icu.text.SimpleDateFormat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker

class DialogDatePicker {

    private var dataPickerDateClickListener: ((month: String, day: String) -> Unit)? = null
    fun setOnDataPickerDateClickListener(f: (month: String, day: String) -> Unit) {
        dataPickerDateClickListener = f
    }

    fun show(fragment : FragmentActivity) {
        val calendar = java.util.Calendar.getInstance()
        val calendarConstrainBuilder = CalendarConstraints.Builder()
        val validators = ArrayList<CalendarConstraints.DateValidator>()
        validators.add(DateValidatorPointForward.now())

        val dataPicker = MaterialDatePicker.Builder.datePicker().setTitleText("Sanani tanlang")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                calendarConstrainBuilder.setStart(System.currentTimeMillis()).build()
            )
            .build()

        dataPicker.addOnPositiveButtonClickListener {
            calendar.timeInMillis = it
            val month = SimpleDateFormat("MM").format(it)
            val day = SimpleDateFormat("dd").format(it)
            dataPickerDateClickListener?.invoke(month,day)
        }

        dataPicker.show(fragment.supportFragmentManager, dataPicker.tag)

    }
}