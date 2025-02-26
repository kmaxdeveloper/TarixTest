package uz.kmax.tarixtest.fragment.tool

import android.content.Intent
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.MainActivity
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.databinding.FragmentLanguageBinding
import uz.kmax.tarixtest.tools.tools.SharedPref

class LanguageFragment : BaseFragmentWC<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    private lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        sharedPref = SharedPref(requireContext())

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