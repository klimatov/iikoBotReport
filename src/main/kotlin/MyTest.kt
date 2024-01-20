import data.repository.GetFromIikoApiRepositoryImpl
import data.repository.GetFromLPApiRepositoryImpl
import data.repository.ReportRepositoryImpl
import domain.models.ReviewsRequestParam
import domain.repository.GetFromLPApiRepository
import domain.usecases.GetDataFromLP
import domain.usecases.GetEmployeesData

class MyTest {
    fun test() {

        println(
            GetDataFromLP(GetFromLPApiRepositoryImpl).getClientData(39131146)
        )

//        val result = GetFromLPApiRepositoryImpl.getReviewList(ReviewsRequestParam(
//            periodFrom = "2024-01-15T17:00:00.000Z",
//            length = 5,
//            partnerID = "2706",
//            withComment = true
//        ))
//        println(result)

//
//        val employeesList = GetEmployeesData(GetFromIikoApiRepositoryImpl()).execute().employees
//        employeesList.forEach {
//            println("${it.name} ${it.birthday}")
//        }


    }
}