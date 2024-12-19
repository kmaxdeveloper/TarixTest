package uz.kmax.tarixtest.fragment.other

import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.UpdateData
import uz.kmax.tarixtest.databinding.FragmentUpdateBinding
import uz.kmax.tarixtest.fragment.main.MenuFragment

class UpdateFragment(location : String): BaseFragmentWC<FragmentUpdateBinding>(FragmentUpdateBinding::inflate) {
    private lateinit var toggleBar: ActionBarDrawerToggle
    private var updateLoc = location
    private val db = Firebase.database
    override fun onViewCreated() {
        // DrawerLayout Code --
        toggleBar = ActionBarDrawerToggle(requireActivity(),binding.drawerLayout,binding.toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggleBar)
        toggleBar.syncState()
        // DrawerLayout Code --

        getMessage(updateLoc)

        // Navigation View Code --
        binding.navigationMenu.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homePage ->{
                    startMainFragment(MenuFragment())
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
                    binding.drawerLayout.isSelected = false
                }

                R.id.devConnection -> {
                    startMainFragment(AdminFragment())
                    closeDrawer()
                }

                R.id.privacyPolicy ->{
                    startMainFragment(PrivacyFragment())
                    closeDrawer()
                }

                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })
        // Navigation View Code --
    }

    private fun getMessage(updateLocation : String){
        val list = ArrayList<UpdateData>()
        db.getReference("TarixTest").child("Message").child("uz").child(updateLocation)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(UpdateData::class.java)
                        h?.let {
                            list.add(UpdateData(it.updateAbout,it.updateTitle))
                        }
                    }
                    ////
                    setData(list)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun setData(data : ArrayList<UpdateData>){
        val getData = ArrayList<UpdateData>()
        getData.addAll(data)
        binding.updateTitle.text = getData[getData.size-1].updateTitle
        binding.updateAbout.text = getData[getData.size-1].updateAbout
    }

    fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START,true)
        }
    }
}