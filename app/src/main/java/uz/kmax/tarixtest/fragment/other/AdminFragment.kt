package uz.kmax.tarixtest.fragment.other

import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.databinding.FragmentAdminBinding
import uz.kmax.tarixtest.fragment.main.MenuFragment

class AdminFragment : BaseFragmentWC<FragmentAdminBinding>(FragmentAdminBinding::inflate) {

    private lateinit var toggleBar: ActionBarDrawerToggle

    override fun onViewCreated() {
        // DrawerLayout Code --
        toggleBar = ActionBarDrawerToggle(requireActivity(),binding.AdminDrawerLayout,binding.toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        binding.AdminDrawerLayout.addDrawerListener(toggleBar)
        toggleBar.syncState()
        // DrawerLayout Code --

        // Navigation View Code --
        binding.navigationAdmin.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.homePage ->{
                    closeDrawer()
                    startMainFragment(MenuFragment())
                }

                R.id.ratingApp -> {
                    val manager = ReviewManagerFactory.create(requireContext())
                    val request = manager.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val reviewInfo = task.result
                            val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                            flow.addOnCompleteListener { result ->
                                if (result.isCanceled){
                                    Toast.makeText(requireContext(), "Dasturni baholash bekor qilindi !", Toast.LENGTH_SHORT).show()
                                }else if (result.isSuccessful) {
                                    Toast.makeText(requireContext(), "Dastur baholandi !!!", Toast.LENGTH_SHORT).show()
                                }else if (result.isComplete){

                                    Toast.makeText(requireContext(), "Baholash tugatildi !", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // There was some problem, log or handle the error code.
                            @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
                        }
                    }
                    closeDrawer()
                }

                R.id.devConnection -> {
                    closeDrawer()
                }

                R.id.privacyPolicy ->{
                    closeDrawer()
                    startMainFragment(PrivacyFragment())
                }

                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })
        // Navigation View Code --
    }

    fun closeDrawer() {
        if (binding.AdminDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.AdminDrawerLayout.closeDrawer(GravityCompat.START,true)
        }
    }
}