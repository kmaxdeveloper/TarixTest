package uz.kmax.tarixtest.presentation.ui.fragment.main.content

import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.Orientation
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.adapter.HistoricalPersonListAdapter
import uz.kmax.tarixtest.data.extension.ImageLoader
import uz.kmax.tarixtest.data.tools.filter.Filter
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.data.tools.tools.onFragmentBackPressed
import uz.kmax.tarixtest.databinding.FragmentHistoricalPersonBinding
import uz.kmax.tarixtest.domain.models.content.HistoricalPersonData
import uz.kmax.tarixtest.domain.models.content.HistoricalPersonListData
import uz.kmax.tarixtest.presentation.ui.fragment.main.MenuFragment
import javax.inject.Inject

@AndroidEntryPoint
class HistoricalPersonFragment(private var path : String) : BaseFragmentWC<FragmentHistoricalPersonBinding>(FragmentHistoricalPersonBinding::inflate) {
    private lateinit var firebaseManager : FirebaseManager
    private var language = ""

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        language = sharedPref.getLanguage().toString()

        onFragmentBackPressed {
            startMainFragment(HistoricalPersonListFragment())
        }

        binding.back.setOnClickListener {
            startMainFragment(HistoricalPersonListFragment())
        }

        getDataFromFirebase()
    }

    private fun getDataFromFirebase() {
        firebaseManager.readData("Content/$language/HistoricalPerson/$path",HistoricalPersonData::class.java){ data, error->
            if (data != null){
                bindDataToView(data)
            }else{
                Toast.makeText(requireContext(), "Error ! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindDataToView(data : HistoricalPersonData){
        binding.personName.text = data.personName
        binding.personBirth.text = data.personBirth
        binding.personDeath.text = data.personDeath
        binding.personType.text = data.personType
        binding.personAwards.text = data.personAwards
        binding.personWorks.text = data.personWorks
        binding.personAbout.text = data.personAboutExtend
        if (data.personType.length > 10){
            binding.personTypeLayout.orientation = LinearLayoutCompat.VERTICAL
            binding.personTypeLayout.gravity = Gravity.CENTER_HORIZONTAL
        }

        ImageLoader.loadImage("Content/HistoricalPerson/${data.personLocation}"){
            if (it != null){
                binding.personImage.setImageBitmap(it)
            }
        }
    }
}