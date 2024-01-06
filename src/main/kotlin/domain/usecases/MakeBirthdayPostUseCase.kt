package domain.usecases

import domain.models.BirthdayParam
import domain.models.EmployeeModel
import domain.repository.BotRepository
import domain.repository.GetFromIikoApiRepository
import utils.Logging
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MakeBirthdayPostUseCase(
    private val getFromIikoApiRepository: GetFromIikoApiRepository,
    private val botRepository: BotRepository
) {
    private val tag = this::class.java.simpleName
    private var employeesList: MutableList<EmployeeModel> = mutableListOf()

    suspend fun execute(birthdayParam: BirthdayParam) {

        // пробуем получить список сотрудников с сервера
        val tempEmployeesList = GetEmployeesData(getFromIikoApiRepository).execute().employees
        // если все ок, то обновляем список
        if (tempEmployeesList.isNotEmpty()) employeesList = tempEmployeesList

        // выделяем в отдельный список тех сотрудников которые попадут в отчет (сразу фильтруем тех у кого не указана дата рождения)
        val celebratingEmployeesList: List<EmployeeModel> = makeCelebratingEmployeesList(
            birthdayParam.sendBeforeDays,
            employeesList.filter { it.birthday != "" }
        )


        celebratingEmployeesList.forEach { employee ->
            val employeeBirthdayValues = makeBirthdayValues(employee.birthday)
            println(
                "${employee.firstName} ${employee.lastName} скоро празднует день рождения - " +
                        "${employeeBirthdayValues.bdDay} ${employeeBirthdayValues.bdMonthWord} " +
                        "исполняется ${employeeBirthdayValues.newAge} ${employeeBirthdayValues.ageYearWord}!!!"
            )
        }


        val sendResult =
            SendBirthdayMessage(botRepository = botRepository).execute(birthdayParam, celebratingEmployeesList)
        Logging.i(
            tag,
            "Напоминание ${birthdayParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
        )


    }

    private fun makeCelebratingEmployeesList(
        sendBeforeDays: Long,
        employeesList: List<EmployeeModel>
    ): List<EmployeeModel> {
        val celebratingEmployeesList: MutableList<EmployeeModel> = mutableListOf()
        val todayDate: LocalDate = LocalDate.now()
        employeesList.forEach { employee ->
            if (checkBirthdayDate(
                    rawSourceDT = employee.birthday,
                    sendBeforeDays = sendBeforeDays,
                    todayDate = todayDate
                )
            ) celebratingEmployeesList.add(employee)
        }
        return celebratingEmployeesList
    }

    data class BirthdayValues(
        val bdYear: String = "",
        val bdMonth: String = "",
        val bdMonthWord: String = "",
        val bdDay: String = "",
        val bdDate: String = "",
        var newAge: Int = 0,
        val ageYearWord: String = ""
    )

    private fun checkBirthdayDate(
        rawSourceDT: String,
        sendBeforeDays: Long,
        todayDate: LocalDate = LocalDate.now()
    ): Boolean {
        try {
            val sourceDate = ZonedDateTime.parse(rawSourceDT).toLocalDate()
            val birthdayDate = LocalDate.of(todayDate.year, sourceDate.month, sourceDate.dayOfMonth)

            return if ((birthdayDate >= todayDate) && (birthdayDate <= todayDate.plusDays(sendBeforeDays)))
                true else false
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }

    private fun makeBirthdayValues(
        rawSourceDT: String,
        todayDate: LocalDate = LocalDate.now()
    ): BirthdayValues {
        val sourceDate = ZonedDateTime.parse(rawSourceDT).toLocalDate()
        val birthdayDate = LocalDate.of(todayDate.year, sourceDate.month, sourceDate.dayOfMonth)

        return BirthdayValues(
            bdYear = sourceDate.year.toString(),
            bdMonth = birthdayDate.monthValue.toString().padStart(2, '0'),
            bdMonthWord = when (birthdayDate.monthValue) {
                1 -> "января"
                2 -> "февраля"
                3 -> "марта"
                4 -> "апреля"
                5 -> "мая"
                6 -> "июня"
                7 -> "июля"
                8 -> "августа"
                9 -> "сентября"
                10 -> "октября"
                11 -> "ноября"
                12 -> "декабря"
                else -> ""
            },
            bdDay = birthdayDate.dayOfMonth.toString(),
            bdDate = birthdayDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            newAge = (todayDate.year - sourceDate.year).toInt(),
            ageYearWord = ageYearWord(todayDate.year - sourceDate.year)
        )
    }

    private fun ageYearWord(age: Int): String {
        return age.let {
            if (it % 100 in 11..14) {
                "лет"
            } else {
                when ((it % 10).toInt()) {
                    1 -> "год"
                    2, 3, 4 -> "года"
                    else -> "лет"//0, 5, 6, 7, 8, 9
                }
            }
        }
    }


}