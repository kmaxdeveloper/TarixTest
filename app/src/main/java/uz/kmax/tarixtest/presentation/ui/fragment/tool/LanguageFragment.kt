package uz.kmax.tarixtest.presentation.ui.fragment.tool

import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.MainActivity
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.databinding.FragmentLanguageBinding
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : BaseFragmentWC<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        binding.selectLangEn.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_en),requireContext())
            sharedPref.setLangStatus(false)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.selectLangUz.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_uz),requireContext())
            sharedPref.setLangStatus(false)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }
}