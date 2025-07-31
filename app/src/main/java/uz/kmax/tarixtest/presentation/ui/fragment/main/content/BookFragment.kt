package uz.kmax.tarixtest.presentation.ui.fragment.main.content

import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.tools.tools.onFragmentBackPressed
import uz.kmax.tarixtest.databinding.FragmentBookBinding
import java.io.File

class BookFragment(private var bookPath : String,private var bookName : String) : BaseFragmentWC<FragmentBookBinding>(FragmentBookBinding::inflate) {
    override fun onViewCreated() {
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.appTheme)
        binding.toolbar.title = bookName
        binding.pdfViewer.initWithFile(File(bookPath))

        binding.back.setOnClickListener {
            startMainFragment(BookListFragment())
        }

        onFragmentBackPressed {
            startMainFragment(BookListFragment())
        }
    }
}