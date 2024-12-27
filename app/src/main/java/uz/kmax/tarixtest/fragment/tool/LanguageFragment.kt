package uz.kmax.tarixtest.fragment.tool

import android.content.Intent
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.MainActivity
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.databinding.FragmentLanguageBinding
import uz.kmax.tarixtest.fragment.welcome.WelcomeFragment
import uz.kmax.tarixtest.tools.other.SharedPref

class LanguageFragment : BaseFragmentWC<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    lateinit var sharedPref: SharedPref
    override fun onViewCreated() {
        sharedPref = SharedPref(requireContext())

        binding.selectLangEn.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_uz),requireContext())
            sharedPref.setLangStatus(false)
            Thread.sleep(2000)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.selectLangUz.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_en),requireContext())
            sharedPref.setLangStatus(false)
            Thread.sleep(2000)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }
}