package uz.kmax.tarixtest.fragment.main

import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.databinding.FragmentMenuBinding
import uz.kmax.tarixtest.expriment.fragment.TestListFragment2
import uz.kmax.tarixtest.fragment.other.AdminFragment
import uz.kmax.tarixtest.fragment.other.PrivacyFragment
import uz.kmax.tarixtest.fragment.tool.SettingsFragment
import uz.kmax.tarixtest.tools.manager.InnerFragmentController

class MenuFragment : BaseFragmentWC<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    private lateinit var toggleBar: ActionBarDrawerToggle
    override fun onViewCreated() {

        InnerFragmentController.init(R.id.innerContainer, requireActivity().supportFragmentManager)
        replaceInnerFragment(TestListFragment())

        toggleBar = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggleBar)
        toggleBar.syncState()

        binding.bottomNavigation.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.appTheme))
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_home -> {
                    // Respond to navigation item 1 click
                    replaceInnerFragment(TestListFragment())
                    true
                }
                R.id.action_content -> {
                    // Respond to navigation item 2 click
                    replaceInnerFragment(ContentFragment())
                    true
                }
                R.id.action_settings ->{
                    // Sozlamalar fragmentiga o'tish
                    replaceInnerFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        binding.navigationMenu.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homePage -> {
                    replaceInnerFragment(TestListFragment())
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.ratingApp -> {
                    val manager = ReviewManagerFactory.create(requireContext())
                    val request = manager.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val reviewInfo = task.result
                            val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                            flow.addOnCompleteListener { result ->
                                if (result.isCanceled) {
                                    toast("Dasturni baholash bekor qilindi !")
                                } else if (result.isSuccessful) {
                                    toast("Dastur baholandi !!!")
                                } else if (result.isComplete) {
                                    toast("Baholash tugatildi !")
                                }
                            }
                        } else {
                            @ReviewErrorCode val reviewErrorCode =
                                (task.exception as ReviewException).errorCode
                        }
                    }
                    closeDrawer()
                    binding.drawerLayout.isSelected = false
                }

                R.id.devConnection -> {
                    replaceInnerFragment(AdminFragment())
                    closeDrawer()
                }

                R.id.privacyPolicy -> {
                    replaceInnerFragment(PrivacyFragment())
                    closeDrawer()
                }

                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })


    }

    private fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START, true)
        }
    }

    private fun toast(message : String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun replaceInnerFragment(fragment : Fragment){
        InnerFragmentController.innerController?.startInnerMainFragment(fragment)
    }
}