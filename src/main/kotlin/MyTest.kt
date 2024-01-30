import data.remoteAPI.twoGis.GetFromTwoGisApiRepositoryImpl
import domain.usecases.GetDataFromTwoGIS

class MyTest {
    fun test() {

        val firmMira = "70000001030737926"
        val firmYarTwentyFive = "70000001023172949"
        val firmYarTwentyThree = "70000001025624980"
        val res = GetDataFromTwoGIS(GetFromTwoGisApiRepositoryImpl).getReviewsList(firmMira)

        res.forEach {
            println("[${it.dateCreated}] ${it.userGISname}: ${it.text}")
        }

//            GetDataFromLP(GetFromLPApiRepositoryImpl).getClientData(39131146)



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