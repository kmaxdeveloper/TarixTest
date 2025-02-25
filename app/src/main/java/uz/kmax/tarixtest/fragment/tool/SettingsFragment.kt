package uz.kmax.tarixtest.fragment.tool

import android.content.Intent
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.MainActivity
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.databinding.FragmentSettingsBinding
import uz.kmax.tarixtest.tools.tools.SharedPref

class SettingsFragment : BaseFragmentWC<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        sharedPref = SharedPref(requireContext())
        val language : String = sharedPref.getLanguage().toString()
        when(language){
            "uz" ->{
                binding.langUz.setBackgroundResource(R.drawable.style_background)
            }

            "en"->{
                binding.langEng.setBackgroundResource(R.drawable.style_background)
            }
        }

        binding.langUz.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_uz),requireContext())
            binding.langUz.setBackgroundResource(R.drawable.style_background)
            binding.langEng.background = null
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.langEng.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_en),requireContext())
            binding.langEng.setBackgroundResource(R.drawable.style_background)
            binding.langUz.background = null
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }
}