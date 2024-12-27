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
import uz.kmax.tarixtest.data.MessageData
import uz.kmax.tarixtest.databinding.FragmentMessageBinding
import uz.kmax.tarixtest.fragment.main.TestListFragment

class MessageFragment(messageLocation : String) : BaseFragmentWC<FragmentMessageBinding>(FragmentMessageBinding::inflate) {
    private var messageLoc = messageLocation
    private val db = Firebase.database

    override fun onViewCreated() {
        getMessage(messageLoc)
    }

    private fun getMessage(messageLocation : String){
        val list = ArrayList<MessageData>()
        db.getReference("TarixTest").child("Message").child("uz").child(messageLocation)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(MessageData::class.java)
                        h?.let {
                            list.add(MessageData(it.message,it.title))
                        }
                    }
                    ////
                    setData(list)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun setData(data : ArrayList<MessageData>){
        val getData = ArrayList<MessageData>()
        getData.addAll(data)
        binding.title.text = getData[getData.size-1].title
        binding.message.text = getData[getData.size-1].message
    }
}