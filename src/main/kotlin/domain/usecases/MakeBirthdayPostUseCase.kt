package domain.usecases

import domain.models.BirthdayParam
import domain.models.EmployeeModel
import domain.repository.BotRepository
import domain.repository.GetFromIikoApiRepository
import utils.Logging

class MakeBirthdayPostUseCase(private val getFromIikoApiRepository: GetFromIikoApiRepository, private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName
    private var employeesList : MutableList<EmployeeModel> = mutableListOf()

    suspend fun execute(birthdayParam: BirthdayParam) {

        // пробуем получить список сотрудников с сервера
        val tempEmployeesList = GetEmployeesData(getFromIikoApiRepository).execute().employees
        // если все ок, то обновляем список
        if (tempEmployeesList.isNotEmpty()) employeesList = tempEmployeesList

        // выделяем в отдельный список тех сотрудников которые попадут в отчет
        val celebratingEmployeesList: List<EmployeeModel> = makeCelebratingEmployeesList(employeesList.filter { it.birthday != "" })


        celebratingEmployeesList.forEachIndexed { index, employeeModel ->
            println("$index ${employeeModel.name} ${employeeModel.birthday}")
        }


        val sendResult = SendBirthdayMessage(botRepository = botRepository).execute(birthdayParam, celebratingEmployeesList)
        Logging.i(
            tag,
            "Напоминание ${birthdayParam.workerName} ${if (sendResult) "отправлено в чат" else "отправить в чат НЕ УДАЛОСЬ"}..."
        )


    }

    private fun makeCelebratingEmployeesList(employeesList: List<EmployeeModel>): List<EmployeeModel> {
        val celebratingEmployeesList : MutableList<EmployeeModel> = mutableListOf()
        employeesList.forEach {employee ->
            //TODO: обработка сотрудников по условию
            celebratingEmployeesList.add(employee)
        }

        return celebratingEmployeesList
    }


}