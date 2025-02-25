package uz.kmax.tarixtest.fragment.other

import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.data.tool.UpdateData
import uz.kmax.tarixtest.databinding.FragmentUpdateBinding
import uz.kmax.tarixtest.tools.firebase.FirebaseManager

class UpdateFragment(location : String): BaseFragmentWC<FragmentUpdateBinding>(FragmentUpdateBinding::inflate) {
    private var updateLoc = location
    private lateinit var firebaseManager: FirebaseManager
    var language = "uz"
    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        getMessage(updateLoc)
    }

    private fun getMessage(updateLocation : String){
        firebaseManager.observeList("Message/$language/$updateLocation", UpdateData::class.java){
            if (it != null) {
                setData(it)
            }
        }
    }

    fun setData(data : ArrayList<UpdateData>){
        val getData = ArrayList<UpdateData>()
        getData.addAll(data)
        binding.updateTitle.text = getData[getData.size-1].updateTitle
        binding.updateAbout.text = getData[getData.size-1].updateAbout
    }
}