package uz.kmax.tarixtest.presentation.ui.fragment.welcome

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.adapter.WelcomeAdapter
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.databinding.FragmentWelcomeBinding
import uz.kmax.tarixtest.presentation.ui.dialog.DialogTestTypeSelection
import uz.kmax.tarixtest.presentation.ui.fragment.welcome.SplashFragment
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment: BaseFragmentWC<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {
    private lateinit var adapter: WelcomeAdapter
    private val dialogTestTypeSelection = DialogTestTypeSelection()

    @Inject
    lateinit var shared: SharedPref

    override fun onViewCreated() {
        shared = SharedPref(requireContext())
        adapter = WelcomeAdapter(requireContext())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.indicator, binding.viewPager) { tab, position -> }.attach()
        binding.start.setOnClickListener {
            shared.setWelcomeStatus(resume = false)
            var statusChoose = shared.getChooseTestType()

            if (statusChoose){
                dialogTestTypeSelection.show(requireContext())
            }else{
                startMainFragment(SplashFragment())
            }
        }

        dialogTestTypeSelection.setOnTypeListener {
            shared.setChooseTestType(false)
            shared.setTestType(it)
        }

        dialogTestTypeSelection.setOnDismissListener {
            startMainFragment(SplashFragment())
        }

        binding.nextButton.setOnClickListener {
            binding.viewPager.currentItem += 1
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                when(position){
                    0->{
                        setText(getString(R.string.welcome_1))
                    }
                    1->{
                        setText(getString(R.string.welcome_2))
                    }
                    2->{
                        setText(getString(R.string.welcome_3))
                    }
                    3->{
                        setText(getString(R.string.welcome_4))
                    }
                    4->{
                        setText(getString(R.string.welcome_5))
                        binding.indicator.visibility = View.INVISIBLE
                        binding.nextButton.visibility = View.INVISIBLE
                        binding.start.visibility = View.VISIBLE
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    fun setText(text : String){
        binding.welcomeText.text = text
    }
}