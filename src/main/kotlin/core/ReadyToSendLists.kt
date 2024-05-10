package core


import models.WorkerParam
import models.WorkerType
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
    private val sendDateTimeMap: MutableMap<LocalDate, MutableList<String>>,
    private val workerType: WorkerType,
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

        if (checkPreliminaryConditions(listDate)) { // если предотправка нужна, то добавляем ее время
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

    private fun checkPreliminaryConditions(listDate: LocalDate): Boolean {
        when (workerParam.preliminarySendBeforeDays > 0) { //если есть предотправка
            (workerType == WorkerType.BIRTHDAY) -> return true //если ДР то добавляем время
            (workerType == WorkerType.REMINDER) -> { //если напоминание
                when (workerParam.sendWhenType){ // 3 - числа месяца, 4 - в даты
                    3 -> {
                        // если в списке отправки по числам есть число даты (сегодня + дней предотправки), то добавляем время
                        return if (workerParam.sendMonthDay.contains(listDate.plusDays(workerParam.preliminarySendBeforeDays).dayOfMonth)) true
                        // иначе проверяем на последний день месяца 32 и (сегодня + дней предотправки) приходится на последнее число месяца, то добавляем время
                        else (workerParam.sendMonthDay.contains(32)) && (isLastDayOfMonth(listDate.plusDays(workerParam.preliminarySendBeforeDays)))
                    }
                    4 -> return sendDateTimeMap.containsKey(listDate.plusDays(workerParam.preliminarySendBeforeDays))

                    else -> return false
                }
            }
            else -> return false
        }
        return false
    }

    private fun isLastDayOfMonth(checkDate: LocalDate): Boolean {
        return (checkDate.lengthOfMonth() == checkDate.dayOfMonth)
    }

    private fun convertToTime(time: String): LocalTime? = try {
        LocalTime.parse(time)
    } catch (e: Exception) {
        Logging.e(tag, "[${workerParam.workerName}] Exception: $e")
        null
    }
}