package domain.usecases

class MakeReportPostUseCase {

    fun execute() {

        // тут получаем отчет
        val result = GetReport().execute()

        // сравниваем с прошлым
        // если изменился, то отправляем в чат


    }
}