package uz.kmax.tarixtest.fragment.main.content

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.adapter.BookListAdapter
import uz.kmax.tarixtest.data.main.BaseBookData
import uz.kmax.tarixtest.databinding.FragmentBookListBinding
import uz.kmax.tarixtest.dialog.DialogBookNotExist
import uz.kmax.tarixtest.fragment.main.MenuFragment
import uz.kmax.tarixtest.tools.file.SaveFiles.saveFileToDownloads
import uz.kmax.tarixtest.tools.file.SaveFiles.saveFileToDownloadsLegacy
import uz.kmax.tarixtest.tools.filter.Filter
import uz.kmax.tarixtest.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.tools.tools.FindFileFromDevice
import uz.kmax.tarixtest.tools.tools.SharedPref
import uz.kmax.tarixtest.tools.tools.onFragmentBackPressed
import java.io.File

class BookListFragment() : BaseFragmentWC<FragmentBookListBinding>(FragmentBookListBinding::inflate){
    private var adapter = BookListAdapter()
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var shared: SharedPref
    private var dataFilter = Filter()
    private var adsStatus = false
    private var language :String = ""
    private var dialog = DialogBookNotExist()
    private var bookPath : String = ""

    override fun onViewCreated() {
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.appTheme)
        firebaseManager = FirebaseManager()
        shared = SharedPref(requireContext())
        language = shared.getLanguage().toString()
        getBookListData()

        binding.bookRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.bookRecycleView.adapter = adapter

        binding.back.setOnClickListener {
            startMainFragment(MenuFragment())
        }

        onFragmentBackPressed {
            startMainFragment(MenuFragment())
        }

        adapter.setOnItemSendListener {
            val path = FindFileFromDevice().findPdfFile("${it.bookLocation}.pdf")
            if (path.isNotEmpty()){
                startMainFragment(BookFragment(path,it.bookName))
            }else{
                dialog.show(requireContext(),it.bookSize)
                dialog.setOnDownloadNowListener {type->
                    when(type){
                        1->{
                            downloadAndSavePDF(requireContext(),"${it.bookLocation}.pdf","TarixTest/Content/SchoolBook/Books/${it.bookLocation}.pdf"){fileUri->
                                if (fileUri.isNotEmpty()){
                                    bookPath = fileUri
                                    dialog.setDownloadInfo("✅ Yuklab olindi !")
                                    dialog.setType(2,"✅ Kitobni ochish")
                                }else{
                                    dialog.setType(1,"Qayta yuklash")
                                    dialog.setDownloadInfo("Xatolik yuz berdi . Kitobni qayta yuklang !")
                                }
                            }
                        }
                        2->{
                            dialog.dismissDialog()
                            startMainFragment(BookFragment(bookPath,it.bookName))
                            //Toast.makeText(requireContext(), bookPath, Toast.LENGTH_SHORT).show()
                        }
                        else->{
                            Toast.makeText(requireContext(), "Xatolik yuz berdi !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun downloadAndSavePDF(context: Context, fileName: String,path : String , onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child(path)

        // 📌 Faylni vaqtincha yuklash
        val tempFile = File.createTempFile("temp_", ".pdf", context.cacheDir)

        // 📌 Firebase'dan faylni yuklab olish
        storageRef.getFile(tempFile)
            .addOnProgressListener { taskSnapshot ->
                dialog.setDownloadInfo("Yuklanmoqda ....")
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                dialog.downloadProgress(progress)
            }
            .addOnSuccessListener {
                // 📌 Android versiyasiga qarab saqlash
                val savedUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveFileToDownloads(context, fileName, tempFile)
                } else {
                    saveFileToDownloadsLegacy(fileName, tempFile)
                }
                onComplete(savedUri)
            }
            .addOnFailureListener { exception ->
                dialog.setType(1,"Xatolik yuz berdi !")
                Log.e("FirebaseDownload", "Xatolik: ${exception.message}")
                onComplete("")
            }
    }

    private fun getBookListData() {
        firebaseManager.observeList("Content/$language/SchoolBook", BaseBookData::class.java) {
            if (it != null) {
                adapter.setItems(dataFilter.filterBook(it))
            }
        }
    }
}