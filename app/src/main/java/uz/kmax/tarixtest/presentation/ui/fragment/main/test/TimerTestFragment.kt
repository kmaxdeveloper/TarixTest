package uz.kmax.tarixtest.presentation.ui.fragment.main.test

import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.tarixtest.R
import uz.kmax.tarixtest.data.ads.AdmobNativeAdsManager
import uz.kmax.tarixtest.data.tools.firebase.FirebaseManager
import uz.kmax.tarixtest.data.ads.AdsManager
import uz.kmax.tarixtest.data.tools.manager.TestManager
import uz.kmax.tarixtest.data.tools.tools.SharedPref
import uz.kmax.tarixtest.data.tools.tools.onFragmentBackPressed
import uz.kmax.tarixtest.domain.models.main.BaseTestData
import uz.kmax.tarixtest.databinding.FragmentTestTimerBinding
import uz.kmax.tarixtest.presentation.ui.dialog.DialogBack
import uz.kmax.tarixtest.presentation.ui.dialog.DialogEndTest
import uz.kmax.tarixtest.presentation.ui.dialog.DialogOverHeart
import uz.kmax.tarixtest.presentation.ui.fragment.main.MenuFragment
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class TimerTestFragment(private var testLocation: String, private var testCount: Int) :
    BaseFragmentWC<FragmentTestTimerBinding>(FragmentTestTimerBinding::inflate) {
    private var testManager: TestManager = TestManager()
    private val testLinearLayouts by lazy { ArrayList<LinearLayoutCompat>() }
    private val variantList by lazy { ArrayList<AppCompatTextView>() }
    private lateinit var testStatus: ArrayList<AppCompatTextView>
    private lateinit var firebaseManager: FirebaseManager
    private var variantSelected = false
    private var testStatusCount = 0
    private var countTest = 0
    private var dialogEnd = DialogEndTest()
    private var dialogBack = DialogBack()
    private var language = "uz"
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 10 * 60 * 1000  // 10 daqiqa
    private var isTimerRunning = false

    @Inject
    lateinit var adsManager: AdsManager

    @Inject
    lateinit var sharedPref: SharedPref
    private var nativeAds = AdmobNativeAdsManager()

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        language = sharedPref.getLanguage().toString()
        /** Reklama yuklash init qilish*/
        adsManager.init()
        nativeAds.init(binding.nativeAdCard,binding.nativeAdView)
        nativeAds.loadNativeAd(requireContext())
        adsManager.initRewardedAds()
        /** Kod oxiri*/
        startTest(testLocation, testCount)

        if (sharedPref.getAnimationStatus()){
            binding.snowAnimation.visibility = View.VISIBLE
            binding.snowAnimation.startSnow()
        }else{
            binding.snowAnimation.visibility = View.GONE
            binding.snowAnimation.stopSnow()
        }

        onFragmentBackPressed {
            timer?.cancel()
            startMainFragment(MenuFragment())
        }
    }

    private fun startTest(testLocation: String, testCount: Int) {
        val randomTest = random(testCount)
        firebaseManager.observeList(
            "Test/$language/$testLocation/V$randomTest",
            BaseTestData::class.java
        ) {
            if (it != null) {
                val listTest = ArrayList<BaseTestData>()
                listTest.addAll(it)
                listTest.shuffle()
                countTest = listTest.size
                testManager.setTestList(listTest)
                loadView()
                loadDataToView()
                timeLeftInMillis = listTest.size.toLong() * 60 * 1000
                timeCounter()
            } else {
                Toast.makeText(requireContext(), "Empty Test !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun variantStyleRestart() {
        for (i in 0 until 4) {
            testLinearLayouts[i].setBackgroundResource(R.drawable.style_test_default_answer)
        }
    }

    private fun loadView() {
        testStatus = ArrayList()
        for (i in 0 until binding.testCountLayout.size) {
            if (i < countTest) {
                testStatus.add(binding.testCountLayout.getChildAt(i) as AppCompatTextView)
            } else {
                binding.testCountLayout.getChildAt(i).visibility = View.GONE
            }
        }
        for (i in 0 until binding.group.childCount) {
            if (binding.group.getChildAt(i) is LinearLayoutCompat) {
                testLinearLayouts.add(binding.group.getChildAt(i) as LinearLayoutCompat)
            }
        }

        variantStyleRestart()

        variantList.add(binding.variantA)
        variantList.add(binding.variantB)
        variantList.add(binding.variantC)
        variantList.add(binding.variantD)

        binding.testCountLayout[positionAnswer()].setBackgroundResource(R.drawable.style_position_answer)

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
        if (variantSelected) {
            if (testManager.hasNextQuestion()) {
                loadDataToView()
                variantStyleRestart()
                binding.testCountLayout[positionAnswer()].setBackgroundResource(R.drawable.style_position_answer)
                variantSelected = false

                if (positionAnswer() == countTest) {
                    binding.nextBtn.text = getText(R.string.finish)
                    binding.nextBtn.textSize = 7f
                }
            } else {
                dialogEnd()
            }
        } else {
            Snackbar.make(binding.nextBtn, "Variantni tanlang !", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.BLUE)
                .setTextColor(Color.WHITE)
                .show()
        }
    }

    private fun loadDataToView() {
        binding.question.text = testManager.getQuestion()
        variantList[0].text = testManager.getVariantA()
        variantList[1].text = testManager.getVariantB()
        variantList[2].text = testManager.getVariantC()
        variantList[3].text = testManager.getVariantD()

        if (positionAnswer() == countTest - 1) {
            binding.nextBtn.text = getText(R.string.finish)
            binding.nextBtn.textSize = 15f
        }

        for (i in 0 until 4) {
            testLinearLayouts[i].setOnClickListener {
                if (!variantSelected) {
                    variantSelected = true
                    check(i)
                } else {
                    Snackbar.make(
                        binding.nextBtn,
                        "Javob belgilangan keyingi savolga o'ting !",
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(Color.GREEN)
                        .setTextColor(Color.WHITE)
                        .show()
                }
            }
        }
    }

    private fun check(position: Int) {
        testManager.checkAnswer(variantList[position].text.toString())
        if (testManager.checkAnswerBoolean(variantList[position].text.toString())) {
            testLinearLayouts[position].setBackgroundResource(R.drawable.style_test_correct_answer)
            binding.testCountLayout.getChildAt(testStatusCount)
                .setBackgroundResource(R.drawable.style_true_answer)
            countUp()
        } else {
            testLinearLayouts[position].setBackgroundResource(R.drawable.style_test_wrong_answer)
            binding.testCountLayout.getChildAt(testStatusCount)
                .setBackgroundResource(R.drawable.style_wrong_answer)
            countUp()
            for (i in 0 until 4) {
                if (testManager.checkAnswerBoolean(variantList[i].text.toString())) {
                    testLinearLayouts[i].setBackgroundResource(R.drawable.style_test_correct_answer)
                }
            }
        }
    }

    private fun countUp() {
        if (testStatusCount == countTest || testStatusCount > countTest) {
            testStatusCount = 0
        } else {
            testStatusCount++
        }
    }

    private fun random(testCount: Int): Int {
        val random = Random.nextInt(0, testCount)
        if (random == 0) {
            return 1
        } else if (random == testCount + 1) {
            return random - 1
        }
        return random
    }

    private fun ads() {
        if (testManager.currentQuestionPosition >= 5) {
            adsManager.showAds(requireActivity()) {
                timer?.cancel()
                startMainFragment(MenuFragment())
            }

            adsManager.setOnAdClickListener {
                Toast.makeText(requireContext(), "Thank you bro !", Toast.LENGTH_SHORT).show()
                timer?.cancel()
                startMainFragment(MenuFragment())
            }

            adsManager.setOnAdDismissListener {
                timer?.cancel()
                startMainFragment(MenuFragment())
            }
        } else {
            timer?.cancel()
            startMainFragment(MenuFragment())
        }
    }

    private fun positionAnswer(): Int {
        if (testStatusCount == countTest) {
            return countTest - 1
        }
        return testStatusCount
    }

    private fun timeCounter(){
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onFinish() {
                dialogEnd()
                timer?.cancel()
            }

            override fun onTick(value: Long) {
                timeLeftInMillis = value
                binding.testTimer.text = longToTime(value)
            }
        }.start()
    }

    fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
    }

    fun resumeTimer() {
        timeCounter()
    }

    private fun longToTime(value: Long): String {
        var minute = (value / 1000) / 60
        var second = (value / 1000) % 60
        var minuteText = if (minute in 0..9) {
            "0$minute"
        } else {
            minute
        }

        var secondText = if (second in 0..9) {
            "0$second"
        } else {
            second
        }
        return "$minuteText:$secondText"
    }

    private fun dialogEnd() {
        pauseTimer()
        dialogEnd.show(
            requireContext(),
            testManager.correctAnswerCount,
            testManager.wrongAnswerCount,
            testType = 1
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
            for (i in 0 until testLinearLayouts.size) {
                testLinearLayouts[i].setBackgroundResource(R.drawable.style_test_default_answer)
            }
            variantSelected = false
            testStatusCount = 0
            testManager.correctAnswerCount = 0
            testManager.wrongAnswerCount = 0
            var testCountSize = testManager.getQuestionSize()
            timeLeftInMillis = (testCountSize * 60 * 1000).toLong()
            timeCounter()
        }
    }
}