package dev.psuchanek.endurancepacecalculator.calculator

open class CalculatorHelper {

    fun generateDurationListOfStrings(secondsByDistance: Float): List<String> {
        val hoursOutput = (secondsByDistance / SECONDS_IN_ONE_HOUR).toInt()
        val minutesOutput =
            ((secondsByDistance - hoursOutput * SECONDS_IN_ONE_HOUR) / SECONDS_IN_ONE_MINUTE).toInt()
        val secondsOutput =
            (secondsByDistance - hoursOutput * SECONDS_IN_ONE_HOUR - minutesOutput * SECONDS_IN_ONE_MINUTE).toInt()
        return listOf(
            hoursOutput.convertToDoubleDigitStringFormat(),
            minutesOutput.convertToDoubleDigitStringFormat(),
            secondsOutput.convertToDoubleDigitStringFormat()
        )
    }

    fun generatePaceListOfStrings(paceInSeconds: Int): List<String> {
        val minutes = paceInSeconds / SECONDS_IN_ONE_MINUTE
        val seconds = paceInSeconds - minutes * SECONDS_IN_ONE_MINUTE
        return listOf(
            minutes.convertToDoubleDigitStringFormat(),
            seconds.convertToDoubleDigitStringFormat()
        )
    }

    fun generatePaceString(paceInSeconds: Int): String {
        val minutes = paceInSeconds / SECONDS_IN_ONE_MINUTE
        val seconds = paceInSeconds - minutes * SECONDS_IN_ONE_MINUTE
        return "${minutes.convertToDoubleDigitStringFormat()}:${seconds.convertToDoubleDigitStringFormat()}"
    }

    private fun Int.convertToDoubleDigitStringFormat(): String = when (this.toString().length) {
        1 -> {
            "0${this}"
        }
        else -> {
            this.toString()
        }
    }


    companion object {
        val DEFAULT_PACE = listOf("00", "00")
        val DEFAULT_DURATION = listOf("00", "00", "00")

        const val RUN_POWER = 101
        const val BIKE_POWER = 102
        const val LTHR = 103
        const val SWIM_PACE = 104
        const val RUN_PACE = 105

        const val SWIM_UNIT_COEF = 100
        const val SECONDS_IN_ONE_HOUR = 3600
        const val SECONDS_IN_ONE_MINUTE = 60

        const val RUN_5K = 5f
        const val RUN_10K = 10f
        const val RUN_HALF_MARATHON = 21.097f
        const val RUN_FULL_MARATHON = 42.195f

        const val SWIM = 111
        const val T1 = 222
        const val BIKE = 333
        const val T2 = 444
        const val RUN = 555

        const val SPRINT_TRI = 11
        const val OLYMPIC_TRI = 22
        const val HALF_DISTANCE_TRI = 33
        const val FULL_DISTANCE_TRI = 44

        const val SPRINT_SWIM_METRIC = 750
        const val SPRINT_BIKE_METRIC = 20
        const val SPRINT_RUN_METRIC = 5

        const val OLYMPIC_SWIM_METRIC = 1500
        const val OLYMPIC_BIKE_METRIC = 40
        const val OLYMPIC_RUN_METRIC = 10

        const val HALF_DISTANCE_SWIM_METRIC = 1900
        const val HALF_DISTANCE_BIKE_METRIC = 90f
        const val HALF_DISTANCE_RUN_METRIC = 21.1f

        const val FULL_DISTANCE_SWIM_METRIC = 3800
        const val FULL_DISTANCE_BIKE_METRIC = 180f
        const val FULL_DISTANCE_RUN_METRIC = 42.2f
    }
}