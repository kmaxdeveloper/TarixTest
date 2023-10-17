package uz.kmax.tarixtest.fragment

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.size
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.ads.GoogleAds
import uz.kmax.tarixtest.data.BaseTestData
import uz.kmax.tarixtest.databinding.FragmentTestBinding
import uz.kmax.tarixtest.dialog.DialogBack
import uz.kmax.tarixtest.dialog.DialogEndTest
import uz.kmax.tarixtest.tools.TestManager
import kotlin.random.Random

class TestFragment(testLocation: String, testCount : Int) : BaseFragmentWC<FragmentTestBinding>(FragmentTestBinding::inflate) {
    private var testLocationFragment: String = testLocation
    private var testCountFragment: Int = testCount
    private var testManager: TestManager = TestManager()
    private val allVariationsViewGroup by lazy { ArrayList<ViewGroup>() }
    private lateinit var testStatus: ArrayList<AppCompatTextView>
    private var selectedVariationImageView: AppCompatImageView? = null
    private var testStatusCount = 0
    private var dialogEnd = DialogEndTest()
    private var dialogBack = DialogBack()
    private val db = Firebase.database
    private var googleAds = GoogleAds()

    override fun onViewCreated() {
        googleAds.initialize(requireContext())
        startTest(testLocationFragment,testCountFragment)
    }

    override fun onResume() {
        super.onResume()
        googleAds.initializeInterstitialAds(requireContext(),getString(R.string.interstitialAdsUnitId))
        googleAds.initializeBanner(binding.bannerAds)
    }

    private fun startTest(testLocation: String,testCount: Int) {
        val listTest = ArrayList<BaseTestData>()
        val randomTest = random(testCount)
        db.getReference("TarixTest").child("Test").child(testLocation).child("V$randomTest")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEachIndexed { _, data ->
                        val h = data.getValue(BaseTestData::class.java)
                        h?.let {
                            listTest.add(
                                BaseTestData(
                                    it.answer,
                                    it.question,
                                    it.variantA,
                                    it.variantB,
                                    it.variantC,
                                    it.variantD
                                )
                            )
                        }
                    }
                    listTest.shuffle()
                    testManager.setTestList(listTest)
                    loadView()
                    loadDataToView()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadView() {
        testStatus = ArrayList()
        for (i in 0 until binding.testCountLayout.size) {
            testStatus.add(binding.testCountLayout.getChildAt(i) as AppCompatTextView)
        }
        for (i in 0 until binding.group.childCount) {
            if (binding.group.getChildAt(i) is LinearLayoutCompat) {
                allVariationsViewGroup.add(binding.group.getChildAt(i) as LinearLayoutCompat)
            }
        }
        binding.back.setOnClickListener {
            dialogBack.show(requireContext())
            dialogBack.setOnBackYesListener {
                ads()
            }
        }
        binding.nextBtn.setOnClickListener {
            next()
        }
        binding.stopTest.setOnClickListener {
            dialogBack.show(requireContext())
            dialogBack.setOnBackYesListener {
                ads()
            }
        }
    }

    fun next() {
        if (selectedVariationImageView != null) {
            val viewGroup = selectedVariationImageView!!.parent as LinearLayoutCompat
            val textview = viewGroup.getChildAt(1) as AppCompatTextView
            testManager.checkAnswer(textview.text.toString())
            if (testStatusCount <= 30) {
                if (testManager.checkAnswerBoolean(textview.text.toString())) {
                    binding.testCountLayout.getChildAt(testStatusCount)
                        .setBackgroundResource(R.drawable.style_true_answer)
                    countUp()
                } else {
                    binding.testCountLayout.getChildAt(testStatusCount)
                        .setBackgroundResource(R.drawable.style_wrong_answer)
                    countUp()
                }
            }
            if (testManager.hasNextQuestion()) {
                loadDataToView()
                selectedVariationImageView = null
            } else {
                dialogEnd.show(
                    requireContext(),
                    testManager.correctAnswerCount,
                    testManager.wrongAnswerCount
                )
                dialogEnd.setOnOkBtnListener {
                    ads()
                }
                dialogEnd.setOnReStartListener {
                    testManager.currentQuestionPosition = 0
                    loadDataToView()
                    for (i in 0 until binding.testCountLayout.size) {
                        binding.testCountLayout.getChildAt(i)
                            .setBackgroundResource(R.drawable.style_test_count)
                    }
                    testStatusCount = 0
                    testManager.correctAnswerCount = 0
                    testManager.wrongAnswerCount = 0
                }
            }
        } else {
            Snackbar.make(binding.nextBtn, "Variantni tanlang !", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.BLUE)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    private fun loadDataToView() {
        uncheck()
        binding.question.text = testManager.getQuestion()
        (allVariationsViewGroup[0].getChildAt(1) as AppCompatTextView).text =
            testManager.getVariantA()
        allVariationsViewGroup[0].setOnClickListener {
            selectVariation(it)
        }
        (allVariationsViewGroup[1].getChildAt(1) as AppCompatTextView).text =
            testManager.getVariantB()
        allVariationsViewGroup[1].setOnClickListener {
            selectVariation(it)
        }
        (allVariationsViewGroup[2].getChildAt(1) as AppCompatTextView).text =
            testManager.getVariantC()
        allVariationsViewGroup[2].setOnClickListener {
            selectVariation(it)
        }
        (allVariationsViewGroup[3].getChildAt(1) as AppCompatTextView).text =
            testManager.getVariantD()
        allVariationsViewGroup[3].setOnClickListener {
            selectVariation(it)
        }
    }

    private fun selectVariation(view: View) {
        val group = view as ViewGroup
        val selectedVariant = group.getChildAt(0) as AppCompatImageView
        uncheck()
        selectedVariant.setImageResource(R.drawable.ic_radio_button_checked_black_24dp)
        selectedVariationImageView = selectedVariant
    }

    private fun uncheck() {
        if (selectedVariationImageView != null) {
            selectedVariationImageView?.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
        }
    }

    private fun countUp() {
        if (testStatusCount == 30 || testStatusCount > 30) {
            testStatusCount = 0
        } else {
            testStatusCount++
        }
    }

    private fun random(testCount: Int):Int{
        val random = Random.nextInt(0,testCount)
        if (random == 0){
            return 1
        }else if (random == testCount+1){
            return random - 1
        }
        return random
    }

    private fun ads(){
        googleAds.showInterstitialAds(requireActivity())
        googleAds.setOnAdsNotReadyListener {
            startMainFragment(MenuFragment())
        }

        googleAds.setOnAdDismissClickListener {
            startMainFragment(MenuFragment())
        }
        googleAds.setOnAdsClickListener {
            Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
        }
    }
}