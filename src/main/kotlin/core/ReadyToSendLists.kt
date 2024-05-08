package core


import models.WorkerParam
import utils.Logging
import java.time.LocalDate
import java.time.LocalTime

data class SendTime(
    val time: LocalTime,
    val preliminarySendBeforeDays: Long = 0, // За сколько дней до события разово отправить (0 - не отправлять)
)

fun MutableList<SendTime>.minOrNull(): SendTime? {
    if (this.isEmpty()) return null else {
        var minTime = this.first()
        this.forEach {
            if (it.time < minTime.time) minTime = it
        }
        return minTime
    }
}



class ReadyToSendLists(
    var workerParam: WorkerParam,
    private val sendDateTimeMap: MutableMap<LocalDate, MutableList<String>>
) {
    private val tag = this::class.java.simpleName
    private var dateOfTheTodayList: LocalDate = LocalDate.now().minusDays(1)
    private var dateOfTheTomorrowList: LocalDate = LocalDate.now().minusDays(1)
    var todayTimeList: MutableList<SendTime> =
        mutableListOf() // список времени отправки сегодня + количество дней предотправки
        get() {
            val todayDate = LocalDate.now()
            if (dateOfTheTodayList != todayDate) { // если список не сегодняшний, то обновляем
                field = makeTimeList(todayDate)
                dateOfTheTodayList = todayDate
            }
            Logging.d(tag, "[${workerParam.workerName}] todayTimeList date: $dateOfTheTodayList todayTimeList: $field")
            return field
        }

    var tomorrowTimeList: MutableList<SendTime> =
        mutableListOf() // список времени отправки завтра + количество дней предотправки
        get() {
            val tomorrowDate = LocalDate.now().plusDays(1)
            if (dateOfTheTomorrowList != tomorrowDate) { // если список не завтрашний, то обновляем
                field = makeTimeList(tomorrowDate)
                dateOfTheTomorrowList = tomorrowDate
            }
            Logging.d(
                tag,
                "[${workerParam.workerName}] tomorrowTimeList date: $dateOfTheTomorrowList tomorrowTimeList: $field"
            )
            return field
        }


    private fun makeTimeList(listDate: LocalDate): MutableList<SendTime> {
        val timeList: MutableList<SendTime> = mutableListOf()

        if (workerParam.preliminarySendBeforeDays > 0) { // если есть предотправка, то добавляем ее время
            timeList.add(
                SendTime(
                    time = convertToTime(workerParam.preliminarySendTime) ?: LocalTime.now(),
                    preliminarySendBeforeDays = workerParam.preliminarySendBeforeDays
                )
            )
        }

        if (workerParam.sendWhenType == 4) { // если тип отправки в указ. даты, то добавляем их
            sendDateTimeMap[listDate]?.forEach { time ->
                convertToTime(time)?.let {
                    timeList.add(
                        SendTime(
                            time = it,
                            preliminarySendBeforeDays = 0
                        )
                    )
                }
            }
        } else { // иначе время отправки
            workerParam.sendTime.forEach { time ->
                convertToTime(time)?.let {
                    timeList.add(
                        SendTime(
                            time = it,
                            preliminarySendBeforeDays = 0
                        )
                    )
                }
            }
        }
        return timeList
    }

    private fun convertToTime(time: String): LocalTime? = try {
        LocalTime.parse(time)
    } catch (e: Exception) {
        Logging.e(tag, "[${workerParam.workerName}] Exception: $e")
        null
    }
}