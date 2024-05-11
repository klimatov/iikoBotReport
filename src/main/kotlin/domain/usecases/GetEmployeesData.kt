package domain.usecases

import domain.models.EmployeeModel
import domain.models.EmployeeResult
import domain.repository.GetFromIikoApiRepository
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

class GetEmployeesData(private val getFromIikoApiRepository: GetFromIikoApiRepository) {
    private val tag = this::class.java.simpleName
    fun execute(): EmployeeResult {
        val doc = getFromIikoApiRepository.getEmployees()
        if (doc != null) {
            val employees = parser(doc)
            return employees
        }
        else return EmployeeResult()
    }

    private fun parser(rawXmlEmployeesString: String): EmployeeResult  {
        val employees = EmployeeResult()
        val doc = Jsoup.parse(rawXmlEmployeesString, Parser.xmlParser())
        for (element in doc.getElementsByTag("employees")) {
            element.children().forEach {
                val newEmployee = EmployeeModel()
                it.children().forEachIndexed() { index, element ->
                    when(element.tag().toString()) {
                        "name" -> newEmployee.name = element.text()
                        "id" -> newEmployee.id = element.text()
                        "firstName" -> newEmployee.firstName = element.text()
                        "middleName" -> newEmployee.middleName = element.text()
                        "lastName" -> newEmployee.lastName = element.text()
                        "phone" -> newEmployee.phone = element.text()
                        "birthday" -> newEmployee.birthday = element.text()
                        "address" -> newEmployee.address = element.text()
                        "hireDate" -> newEmployee.hireDate = element.text()
                        "login" -> newEmployee.login = element.text()
                        "cellPhone" -> newEmployee.cellPhone = element.text()
                        "note" -> newEmployee.note = element.text()
                        "cardNumber" -> newEmployee.cardNumber = element.text()
                        "client" -> newEmployee.client = element.text()
                        "code" -> newEmployee.code = element.text()
                        "deleted" -> newEmployee.deleted = element.text()
                        "departmentCodes" -> newEmployee.departmentCodes = element.text()
                        "employee" -> newEmployee.employee = element.text()
                        "mainRoleCode" -> newEmployee.mainRoleCode = element.text()
                        "mainRoleId" -> newEmployee.mainRoleId = element.text()
                        "preferredDepartmentCode" -> newEmployee.preferredDepartmentCode = element.text()
                        "responsibilityDepartmentCodes" -> newEmployee.responsibilityDepartmentCodes = element.text()
                        "snils" -> newEmployee.snils = element.text()
                        "supplier" -> newEmployee.supplier = element.text()
                        "taxpayerIdNumber" -> newEmployee.taxpayerIdNumber = element.text()
                        "roleCodes" -> newEmployee.roleCodes.add(element.text())
                        "rolesIds" -> newEmployee.rolesIds.add(element.text())
                    }
                }
                employees.employees.add(newEmployee)
            }
        }
        return employees
    }
}