package webServer

import kotlinx.html.*
import models.BundleParam

fun FORM.sendChatIdField(fieldValues: List<Long>, nameIdBundleList: List<BundleParam>) {
    val tag = "sendChatIdField"
    div(classes = "field") {
        label(classes = "label required") {
            +"ID чата/юзера куда будут отправляться данные"
        }
        ul(classes = "checkboxes") {
            nameIdBundleList.forEach { nameIdBundle ->
                li(classes = "checkbox") {
                    input(
                        type = InputType.checkBox,
                        classes = "checkbox-input",
                        name = "sendChatId"
                    ) {
                        value = nameIdBundle.telegramId.toString()
                        id = "tgid-${nameIdBundle.telegramId}"
                        checked = fieldValues.contains(value.toLongOrNull())
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

