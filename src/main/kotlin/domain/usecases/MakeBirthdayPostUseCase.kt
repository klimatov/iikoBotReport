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

        val sendResult =
            SendBirthdayMessage(botRepository = botRepository).execute(birthdayParam, celebratingEmployeesList)
        Logging.i(
            tag,
            "Напоминание о ДР ${birthdayParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
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
}