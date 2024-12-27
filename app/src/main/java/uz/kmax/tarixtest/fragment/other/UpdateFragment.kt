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
import uz.kmax.tarixtest.fragment.main.TestListFragment

class UpdateFragment(location : String): BaseFragmentWC<FragmentUpdateBinding>(FragmentUpdateBinding::inflate) {
    private var updateLoc = location
    private val db = Firebase.database
    override fun onViewCreated() {
        getMessage(updateLoc)
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
}