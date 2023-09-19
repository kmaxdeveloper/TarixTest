package uz.kmax.tarixtest.tools

import uz.kmax.tarixtest.data.BaseTestData


class TestManager() {
    var currentQuestionPosition: Int = 0
    var correctAnswerCount = 0
    var wrongAnswerCount = 0
    var percent = 0
    private var testQuestionsList = ArrayList<BaseTestData>()

    fun setTestList(testList : ArrayList<BaseTestData>){
        testQuestionsList.clear()
        testQuestionsList.addAll(testList)
    }

    fun getQuestion(): String {
        return testQuestionsList[currentQuestionPosition].question
    }

    fun getAnswer(): String {
        return testQuestionsList[currentQuestionPosition].answer
    }

    fun getVariantA(): String {
        return testQuestionsList[currentQuestionPosition].variantA
    }

    fun getVariantB(): String {
        return testQuestionsList[currentQuestionPosition].variantB
    }

    fun getVariantC(): String {
        return testQuestionsList[currentQuestionPosition].variantC
    }

    fun getVariantD(): String {
        return testQuestionsList[currentQuestionPosition].variantD
    }

    fun getQuestionSize() = testQuestionsList.size

    fun hasNextQuestion(): Boolean {
        if (currentQuestionPosition < getQuestionSize() - 1) {
            currentQuestionPosition++
            return true
        }
        return false
    }

    fun checkAnswer(answer: String) {
        if (answer == getAnswer()) {
            correctAnswerCount++
        } else {
            wrongAnswerCount++
        }
    }

    fun checkAnswerBoolean(answer: String) : Boolean{
        if (answer == getAnswer()) {
            return true
        }
        return false
    }

    fun answerWithPercent() = correctAnswerCount * 100 / getQuestionSize()

}