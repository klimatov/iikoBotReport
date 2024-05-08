package domain.usecases

import domain.models.BirthdayParam
import domain.models.EmployeeModel
import domain.repository.BotRepository
import domain.repository.GetFromIikoApiRepository
import utils.Logging
import java.time.LocalDate
import java.time.ZonedDateTime

class MakeBirthdayPostUseCase(
    private val getFromIikoApiRepository: GetFromIikoApiRepository,
    private val botRepository: BotRepository
) {
    private val tag = this::class.java.simpleName
    private var employeesList: MutableList<EmployeeModel> = mutableListOf()

    suspend fun execute(birthdayParam: BirthdayParam, preliminary: Boolean = false) {
        // пробуем получить список сотрудников с сервера
        val tempEmployeesList = GetEmployeesData(getFromIikoApiRepository).execute().employees
        // если все ок, то обновляем список
        if (tempEmployeesList.isNotEmpty()) employeesList = tempEmployeesList

        val celebratingEmployeesList: List<EmployeeModel> =
            // выделяем в отдельный список тех сотрудников которые попадут в отчет (сразу фильтруем тех у кого не указана дата рождения)
            // при предотправке - в списке только те, у кого дней до ДР = количеству дней указанных в предотправке
            makeCelebratingEmployeesList(
                birthdayParam = birthdayParam,
                preliminary = preliminary,
                employeesList = employeesList.filter { it.birthday != "" },
            )

        val sendResult =
            SendBirthdayMessage(botRepository = botRepository).execute(birthdayParam, celebratingEmployeesList)
        Logging.i(
            tag,
            "Напоминание о ДР ${birthdayParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
        )
    }

    private fun makeCelebratingEmployeesList(
        birthdayParam: BirthdayParam,
        preliminary: Boolean,
        employeesList: List<EmployeeModel>,
    ): List<EmployeeModel> {
        val celebratingEmployeesList: MutableList<EmployeeModel> = mutableListOf()
        val todayDate: LocalDate = LocalDate.now()
        employeesList.forEach { employee ->
            if (checkBirthdayDate(
                    birthdayParam = birthdayParam,
                    preliminary = preliminary,
                    rawSourceDT = employee.birthday,
                    todayDate = todayDate,
                )
            ) celebratingEmployeesList.add(employee)
        }
        return celebratingEmployeesList
    }

    private fun checkBirthdayDate(
        birthdayParam: BirthdayParam,
        preliminary: Boolean,
        rawSourceDT: String,
        todayDate: LocalDate = LocalDate.now(),
    ): Boolean {
        try {
            val sourceDate = ZonedDateTime.parse(rawSourceDT).toLocalDate()
            val birthdayDate = LocalDate.of(todayDate.year, sourceDate.month, sourceDate.dayOfMonth)

            return if (birthdayDate >= todayDate) {
                when {
                    (!preliminary) && (birthdayDate <= todayDate.plusDays(birthdayParam.sendBeforeDays)) -> true
                    (preliminary) && (birthdayDate == todayDate.plusDays(birthdayParam.preliminarySendBeforeDays)) -> true
                    else -> false
                }
            } else false
        } catch (e: Exception) {
            Logging.e(tag, e.toString())
            return false
        }
    }
}