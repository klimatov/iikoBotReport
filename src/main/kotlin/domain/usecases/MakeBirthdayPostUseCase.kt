package domain.usecases

import domain.repository.BotRepository
import domain.repository.GetFromIikoApiRepository

class MakeBirthdayPostUseCase(private val getFromIikoApiRepository: GetFromIikoApiRepository, private val botRepository: BotRepository) {
    private val tag = this::class.java.simpleName


}