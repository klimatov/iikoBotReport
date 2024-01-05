package domain.models

data class EmployeeResult(
    var employees: MutableList<EmployeeModel> = mutableListOf()
)

data class EmployeeModel(
    var name: String = "",
    var id: String = "",
    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",
    var phone: String = "",
    var birthday: String = "",
    var address: String = "",
    var hireDate: String = "",
    var login: String = "",
    var cellPhone: String = "",
    var note: String = "",
    var cardNumber: String = "",
    var client: String = "",
    var code: String = "",
    var deleted: String = "",
    var departmentCodes: String = "",
    var employee: String = "",
    var mainRoleCode: String = "",
    var mainRoleId: String = "",
    var preferredDepartmentCode: String = "",
    var responsibilityDepartmentCodes: String = "",
    var snils: String = "",
    var supplier: String = "",
    var taxpayerIdNumber: String = "",
    var roleCodes: MutableList<String> = mutableListOf(),
    var rolesIds: MutableList<String> = mutableListOf()
)