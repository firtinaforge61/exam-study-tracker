package com.examtracker.app.data.local

object NetCalculationRuleKeys {

    const val FOUR_WRONG_ONE_CORRECT = "FOUR_WRONG_ONE_CORRECT"
    const val THREE_WRONG_ONE_CORRECT = "THREE_WRONG_ONE_CORRECT"
    const val NO_EFFECT = "NO_EFFECT"

    fun calculateNet(
        rule: String?,
        correctCount: Int,
        wrongCount: Int
    ): Double {
        return when (rule) {
            FOUR_WRONG_ONE_CORRECT ->
                correctCount - (wrongCount / 4.0)

            THREE_WRONG_ONE_CORRECT ->
                correctCount - (wrongCount / 3.0)

            NO_EFFECT ->
                correctCount.toDouble()

            else ->
                correctCount.toDouble()
        }
    }
}