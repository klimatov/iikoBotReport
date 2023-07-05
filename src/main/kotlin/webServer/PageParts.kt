package webServer

import kotlinx.html.*
import models.BundleParam

fun FORM.sendChatIdField(fieldValue: List<String>, nameIdBundleList: List<BundleParam>) {
    div(classes = "field") {
//    p(classes = "field required half") {
        label(classes = "label required") {
            +"ID чата/юзера куда будут отправляться данные"
        }
//        input(type = InputType.number, name = "sendChatId", classes = "text-input") {
//            value = fieldValue
//            required = true
//        }


        ul(classes = "checkboxes") {

            nameIdBundleList.forEach {nameIdBundle ->
                li(classes = "checkbox") {
                    input(
                        type = InputType.checkBox,
                        classes = "checkbox-input",
                        name = "sendChatId"
                    ) {
                        value = nameIdBundle.telegramId.toString()
                        id = "tgid-${nameIdBundle.telegramId}"
                        checked = fieldValue.contains(value)
                    }
                    label(classes = "checkbox-label") {
                        onClick = "function setBundleCheckbox() {\n" +
                                "var c = document.querySelector('#tgid-${nameIdBundle.telegramId}');\n" +
                                "c.checked = !c.checked }\n" +
                                "setBundleCheckbox();"
                        +nameIdBundle.name
                    }
                }



            }

        }
    }


}

