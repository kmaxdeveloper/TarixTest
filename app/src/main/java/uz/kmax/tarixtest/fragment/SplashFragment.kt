package uz.kmax.tarixtest.fragment

import android.os.CountDownTimer
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.tools.Connection
import uz.kmax.tarixtest.databinding.FragmentSplashBinding
import uz.kmax.tarixtest.dialog.DialogConnection


class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate){
    var connectionDialog = DialogConnection()
    override fun onViewCreated() {
        object : CountDownTimer(5000, 100) {
            override fun onFinish() {
                if(Connection().check(requireContext())){
                    startMainFragment(MenuFragment())
                }else{
                    connectionDialog.show(requireContext())
                    connectionDialog.setOnCloseListener {
                        activity?.finish()
                    }
                    connectionDialog.setOnTryAgainListener {
                        if (Connection().check(requireContext())){
                            startMainFragment(MenuFragment())
                        }else{
                            connectionDialog.show(requireContext())
                        }
                    }
                }
            }

            override fun onTick(value: Long) {

            }
        }.start()
    }
}