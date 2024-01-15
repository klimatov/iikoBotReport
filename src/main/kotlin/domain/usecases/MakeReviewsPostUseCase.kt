package domain.usecases

import domain.models.BirthdayParam
import domain.models.EmployeeModel
import domain.repository.BotRepository
import domain.repository.GetFromIikoApiRepository
import domain.repository.GetFromLPApiRepository
import utils.Logging
import java.time.LocalDate
import java.time.ZonedDateTime

class MakeReviewsPostUseCase(
    private val getFromLPApiRepository: GetFromLPApiRepository,
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