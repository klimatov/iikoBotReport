package webServer

import kotlinx.html.*

fun FORM.sendChatIdField(fieldValue: String) {
    p(classes = "field required half") {
        label(classes = "label required") {
            +"ID чата/юзера куда будут отправляться данные"
        }
        input(type = InputType.number, name = "sendChatId", classes = "text-input") {
            value = fieldValue
            required = true
        }
    }

}