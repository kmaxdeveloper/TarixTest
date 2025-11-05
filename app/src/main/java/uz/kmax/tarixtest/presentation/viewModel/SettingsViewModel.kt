package uz.kmax.tarixtest.presentation.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val isSnowing = MutableLiveData<Boolean>()
}
