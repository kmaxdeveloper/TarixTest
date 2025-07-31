package uz.kmax.tarixtest.presentation.ui.fragment.tool

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.MainActivity
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.databinding.FragmentSettingsBinding
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragmentWC<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onViewCreated() {
        sharedPref = SharedPref(requireContext())
        val language : String = sharedPref.getLanguage().toString()
        when(language){
            "uz" ->{
                binding.langUz.setBackgroundResource(R.drawable.style_background)
                binding.langUzSelected.visibility = View.VISIBLE
            }

            "en"->{
                binding.langEng.setBackgroundResource(R.drawable.style_background)
                binding.langEnSelected.visibility = View.VISIBLE
            }
        }

        setTestType()

        binding.infinityHeartBtn.setOnClickListener {
            sharedPref.setTestType(1)
            setTestType()
            showSnackBar(it)
        }

        binding.costHeartBtn.setOnClickListener {
            sharedPref.setTestType(3)
            setTestType()
            showSnackBar(it)
        }

        binding.langUz.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_uz),requireContext())
            binding.langUz.setBackgroundResource(R.drawable.style_background)
            binding.langEng.background = null
            binding.langEnSelected.visibility = View.GONE
            binding.langUzSelected.visibility = View.VISIBLE
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.langEng.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_en),requireContext())
            binding.langEng.setBackgroundResource(R.drawable.style_background)
            binding.langUz.background = null
            binding.langUzSelected.visibility = View.GONE
            binding.langEnSelected.visibility = View.VISIBLE
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showSnackBar(view: View){
        Snackbar.make(view, "Test rejimi muaffaqiyatli o'zgartirildi !", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.BLUE)
            .setTextColor(Color.WHITE)
            .show()
    }

    private fun setTestType(){
        val testType : Int = sharedPref.getTestType()
        when(testType){
            1->{
                binding.testTypeThreeHeart.visibility = View.GONE
                binding.testTypeInfinityHeart.visibility = View.VISIBLE
            }
            3->{
                binding.testTypeThreeHeart.visibility = View.VISIBLE
                binding.testTypeInfinityHeart.visibility = View.GONE
            }
        }
    }
}