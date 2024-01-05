import data.repository.GetFromIikoApiRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.usecases.GetEmployeesData

class MyTest {
    fun test() {
        val employeesList = GetEmployeesData(GetFromIikoApiRepositoryImpl()).execute().employees
        employeesList.forEach {
            println("${it.name} ${it.birthday}")
        }


    }
}