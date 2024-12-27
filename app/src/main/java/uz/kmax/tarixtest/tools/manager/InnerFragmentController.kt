package uz.kmax.tarixtest.tools.manager

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class InnerFragmentController private constructor(
    @IdRes private val container: Int,
    private val fragmentManager: FragmentManager
) {

    fun startInnerMainFragment(fr: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(container, fr)
        transaction.commit()
    }

    fun replaceInnerFragment(fr: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(container, fr)
        transaction.addToBackStack(fr.toString())
        transaction.commit()
    }

    fun addInnerFragment(fr: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(container, fr)
        transaction.addToBackStack(fr.toString())
        transaction.commit()
    }

    fun backInner() {
        fragmentManager.popBackStack()
    }

    fun getLastInnerFragment(): Fragment {
        return fragmentManager.fragments[fragmentManager.fragments.size - 1]
    }

    companion object {
        var innerController: InnerFragmentController? = null
            private set

        fun init(@IdRes contentId: Int, fm: FragmentManager) {
            innerController = InnerFragmentController(contentId, fm)
        }
    }
}