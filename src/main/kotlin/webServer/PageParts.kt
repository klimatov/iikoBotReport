package webServer

import kotlinx.html.*
import models.BundleParam
import models.ReminderWorkerParam

fun FORM.workerIdField(workerId: String, workerIsActive: Boolean, workerTypeName: String) {
    val fieldName = "workerIsActive"
    p(classes = "field") {
        input(type = InputType.checkBox, name = fieldName, classes = "checkbox-input") {
            checked = workerIsActive
            id = fieldName
        }
        label(classes = "checkbox-label") {
            title = "Клик для включения/отключения активности"
            onClick = "setWorkerIdCheckbox('$fieldName');"
            +"$workerTypeName с ID: $workerId ${if (workerIsActive) "вкл." else "выкл."}"
        }
    }
    hiddenInput {
        name = "workerId"
        value = workerId
    }
}

fun FORM.workerNameField(workerName: String, nameInHeader: Boolean, workerTypeName: String) {
    p(classes = "field required half") {
        label(classes = "label required") {
            +"Название $workerTypeName"
        }
        input(type = InputType.text, name = "workerName", classes = "text-input") {
            value = workerName
            required = true
            id = "workerName"
        }
    }
    p(classes = "field half") {
        input(type = InputType.checkBox, name = "nameInHeader", classes = "checkbox-input") {
            checked = nameInHeader
            id = "nameInHeader"
        }
        label(classes = "checkbox-label") {
            title = "Клик для включения/отключения вывода названия в заголовке сообщения"
            onClick = "setCheckbox('nameInHeader');"
            +"Выводить в заголовке сообщения"
        }
    }
}

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

fun FORM.sendWhenTypeField(sendWhenType: Int, workerTypeName: String) {
    p(classes = "field half") {
        label(classes = "label") {
            +"Когда отправлять $workerTypeName" //"1 - периодически, 2 - дни недели, 3 - числа месяца, 0 - ежедневно"
        }
        select(classes = "select") {
            name = "sendWhenType"
            onChange = "onSelectWhenType(this)"
            option {
                value = "1"
                selected = sendWhenType.toString() == value
                +"Периодически"
            }
            option {
                value = "0"
                selected = sendWhenType.toString() == value
                +"Ежедневно"
            }
            option {
                value = "2"
                selected = sendWhenType.toString() == value
                +"Дни недели"
            }
            option {
                value = "3"
                selected = sendWhenType.toString() == value
                +"Числа месяца"
            }
        }
    }
}

fun FORM.sendPeriodField(sendPeriod: Int) {
    p(classes = "field half") {
        id = "sendPeriod"
        label(classes = "label") {
            +"Период отправки в минутах"
        }
        input(type = InputType.number, name = "sendPeriod", classes = "text-input") {
            min = "1"
            max = "1440"
            value = sendPeriod.toString()
        }
    }
}

fun FORM.sendTimeField(sendTime: List<String>) {
    p(classes = "field half") {
        id = "sendTime"
        label(classes = "label") {
            +"Время отправки"
        }
        input(type = InputType.time, name = "sendTime", classes = "text-input") {
            value = sendTime.joinToString()
        }
    }
}

fun FORM.sendWeekDayField(sendWeekDay: List<Int>) {
    div(classes = "field") {
        id = "sendWeekDay"
        label(classes = "label") {
            +"Дни недели для отправки напоминания"
        }
        ul(classes = "checkboxes") {
            val daysOfWeek = listOf(
                "Понедельник",
                "Вторник",
                "Среда",
                "Четверг",
                "Пятница",
                "Суббота",
                "Воскресенье"
            )

            for (day in 1..7) {
                li(classes = "checkbox") {
                    input(
                        type = InputType.checkBox,
                        classes = "checkbox-input",
                        name = "sendWeekDay"
                    ) {
                        value = day.toString()
                        id = "sendWeekDay-${day}"
                        checked = sendWeekDay.toString().contains(value)
                    }
                    label(classes = "checkbox-label") {
                        onClick = "setCheckbox('sendWeekDay-${day}')"
                        +daysOfWeek[day - 1]
                    }
                }
            }
        }
    }
}


fun FORM.sendMonthDay(sendMonthDay: List<Int>) {
    div(classes = "field") {
        id = "sendMonthDay"
        label(classes = "label") {
            +"Числа месяца для отправки (32 - в последний день месяца)"
        }
        ul(classes = "checkboxes") {
            for (day in 1..32) {
                li(classes = "checkbox") {
                    input(
                        type = InputType.checkBox,
                        classes = "checkbox-input",
                        name = "sendMonthDay"
                    ) {
                        value = day.toString()
                        id = "sendMonthDay-${day}"
                        checked = sendMonthDay.contains(value.toInt())
                    }
                    label(classes = "checkbox-label") {
                        onClick = "setCheckbox('sendMonthDay-${day}');"
                        +if (day < 10) "0$day" else "$day"
                    }
                }
            }
        }
    }
}

fun FORM.bottomButtonsField() {
    p(classes = "field half") {
        ul(classes = "options") {
            li(classes = "option") {
                input(type = InputType.submit, classes = "button") {
                    name = "saveButton"
                    value = "Сохранить"
                }
            }
            li(classes = "option") {
                input(type = InputType.submit, classes = "button") {
                    name = "deleteButton"
                    value = "Удалить"
                }
            }
            li(classes = "option") {
                input(type = InputType.button, classes = "button") {
                    name = "backButton"
                    onClick = "history.back()"
                    value = "Назад"
                }
            }
        }
    }
}