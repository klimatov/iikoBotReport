package core

import models.WorkerParam
import utils.Logging
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ReadyToSendLists(
    var workerParam: WorkerParam,
    private val sendDateTimeMap: MutableMap<LocalDate, MutableList<String>>
) {
    private val tag = this::class.java.simpleName
    private var dateOfTheTodayList: LocalDate = LocalDate.now().minusDays(1)
    private var dateOfTheTomorrowList: LocalDate = LocalDate.now().minusDays(1)
    var todayTimeList: MutableMap<LocalDateTime, Long> = mutableMapOf() // список времени отправки сегодня + количество дней предотправки
        get() {
            val todayDate = LocalDate.now()
            if (dateOfTheTodayList != todayDate) { // если список не сегодняшний, то обновляем
                field = makeTimeList(todayDate)
                dateOfTheTodayList = todayDate
            }
            Logging.d(tag, "[${workerParam.workerName}] todayTimeList date: $dateOfTheTodayList todayTimeList: $field")
            return field
        }

    var tomorrowTimeList: MutableMap<LocalDateTime, Long> = mutableMapOf() // список времени отправки завтра + количество дней предотправки
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

    private fun makeTimeList(listDate: LocalDate): MutableMap<LocalDateTime, Long> {
        val timeList: MutableMap<LocalDateTime, Long> = mutableMapOf()

        if (workerParam.preliminarySendBeforeDays > 0) { // если есть предотправка, то добавляем ее время
            val preliminarySendTime =
                convertToTime(workerParam.preliminarySendTime) ?: LocalTime.now()
            timeList[listDate.atTime(preliminarySendTime)] =
                workerParam.preliminarySendBeforeDays
        }

        if (workerParam.sendWhenType == 4) { // если тип отправки в указ.даты, то добавляем их
            sendDateTimeMap[listDate]?.forEach { time ->
                convertToTime(time)?.let { timeList[listDate.atTime(it)] = 0 }
            }
        } else { // иначе время отправки
            workerParam.sendTime.forEach { time ->
                convertToTime(time)?.let { timeList[listDate.atTime(it)] = 0 }
            }
        }
        return timeList.toSortedMap()
    }

    private fun convertToTime(time: String): LocalTime? = try {
        LocalTime.parse(time)
    } catch (e: Exception) {
        Logging.e(tag, "[${workerParam.workerName}] Exception: $e")
        null
    }
}