package uz.kmax.tarixtest.tools.tools

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.onBackPressed(
    doubleBackToExit: Boolean = false,  // 🔹 Ikki marta bosish orqali chiqish
    confirmExitDialog: Boolean = false, // 🔹 Chiqishdan oldin tasdiqlash oynasi
    onBackPressedAction: (() -> Unit)? = null  // 🔹 Maxsus back funksiyasi
) {
    var backPressedTime: Long = 0

    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when {
                onBackPressedAction != null -> onBackPressedAction.invoke()
                doubleBackToExit -> handleDoubleBackPress()
                confirmExitDialog -> showExitConfirmationDialog()
                else -> finish()
            }
        }

        private fun handleDoubleBackPress() {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                Toast.makeText(this@onBackPressed, "Chiqish uchun yana bir marta bosing", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }

        private fun showExitConfirmationDialog() {
            AlertDialog.Builder(this@onBackPressed)
                .setTitle("Chiqishni tasdiqlang")
                .setMessage("Siz haqiqatdan ham dasturni yopmoqchimisiz?")
                .setPositiveButton("Ha") { _, _ -> finish() }
                .setNegativeButton("Yo'q", null)
                .show()
        }
    })
}

// 🔹 Fragment ichida "Back" tugmasini boshqarish uchun extension
fun Fragment.onFragmentBackPressed(
    onBackPressedAction: (() -> Unit)? = null
) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedAction?.invoke()
        }
    })
}

// 🔹 Fragmentdan boshqa Fragmentga ma'lumot jo'natish
fun Fragment.sendFragmentResult(requestKey: String, data: Bundle) {
    parentFragmentManager.setFragmentResult(requestKey, data)
}

// 🔹 Boshqa Fragmentdan ma'lumotni olish
fun Fragment.getFragmentResult(requestKey: String, onResult: (Bundle) -> Unit) {
    parentFragmentManager.setFragmentResultListener(requestKey, viewLifecycleOwner) { _, bundle ->
        onResult(bundle)
    }
}