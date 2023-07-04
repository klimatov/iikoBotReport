package webServer

import data.fileProcessing.NameIdBundleRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.html.*
import models.BundleParam
import models.IdAvailability
import utils.Logging
import java.io.File

fun Application.configureEditNameIdBundle() {
    val tag = "configureEditNameIdBundle"
    routing {
        authenticate("auth-basic") {
            static("/") {
                staticRootFolder = File("")
                files("css")
            }
            get("/edit-name-id-bundle") {
                val bundleParamList = NameIdBundleRepository.get()
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"iikoBotReport edit name id bundle"
                        }
                        meta {
                            name = "viewport"
                            content = "width=device-width, initial-scale=1"
                        }
                        link(
                            rel = "stylesheet",
                            href = "https://cdnjs.cloudflare.com/ajax/libs/normalize/5.0.0/normalize.min.css"
                        )
                        link(
                            rel = "stylesheet",
                            href = "main.css"
                        )
                    }
                    body {
                        postForm(classes = "form") {

                            ul(classes = "ul") {
                                newBundle() // добавляем скрытый шаблон
                                bundleParamList.forEach { bundleParam ->
                                    newBundle(bundleParam)
                                }
                            }

                            //!!!!! ---------------------------------------------------------------------------------------------------------------

                            p(classes = "field half") {

                                ul(classes = "options") {

                                    li(classes = "option") {
                                        input(type = InputType.submit, classes = "button") {
                                            name = "saveButton"
                                            value = "Сохранить"
                                        }
                                    }

                                    li(classes = "option") {
                                        input(type = InputType.button, classes = "button") {
                                            name = "addButton"
                                            onClick =
//                                                "addButtonPress(this);"
                                                "addButtonPress();"
                                            value = "Добавить"
                                        }
                                        hiddenInput {
                                            id = "counter"
                                            value =
                                                "${(bundleParamList.maxWithOrNull(Comparator.comparingInt { it.botUserId })?.botUserId ?: 0)}"
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

//!!!!! ---------------------------------------------------------------------------------------------------------------

                        }


                        script(type = "text/javascript", src = "js/editBundle.js") {}


                    }
                }
            }

            post("/edit-name-id-bundle") {
//                Logging.d(tag, call.receiveParameters().toString())
                val bundleParamList: MutableList<BundleParam> = mutableListOf()
                val receiveParam: Map<String, String> =
                    call.receiveParameters()
                        .toMap()
                        .mapValues { (_, values) -> values.first() }
                        .filter { (key, _) ->
                            key.startsWith("name") || key.startsWith("tgid") || key.startsWith("id")
                        }
                Logging.d(tag, receiveParam.toString())
                receiveParam.filter { (key, _) -> key.startsWith("id") }.forEach { (key, value) ->
                    if (value == "0") {
                        return@forEach
                    }
                    val botUserId = value.toIntOrNull() ?: 0
                    bundleParamList.add(
                        BundleParam(
                            botUserId = botUserId,
                            name = receiveParam["name$botUserId"] ?: "",
                            telegramId = receiveParam["tgid$botUserId"]?.toLongOrNull() ?: 0,
                            available = IdAvailability.NOT_CHECKED
                        )
                    )
                }

                Logging.d(tag, bundleParamList.toString())

                NameIdBundleRepository.set(bundleParamList)


                val userIP = call.request.origin.remoteHost
                val userName = call.principal<UserIdPrincipal>()?.name

                call.respondRedirect("/edit-name-id-bundle")
            }


        }
    }
}

private fun UL.newBundle(
    bundleParam: BundleParam = BundleParam(
        botUserId = 0,
        name = "",
        telegramId = 0,
        available = IdAvailability.NOT_CHECKED
    )
) {
    li(classes = "li") {
        if (bundleParam.botUserId == 0) { // скрытый шаблон для новых строк
            id = "template"
        }
        p(classes = "field withicons") {
            input(type = InputType.text, name = "name${bundleParam.botUserId}", classes = "text-input") {
                value = bundleParam.name
                title = "Имя чата/пользователя"
//                id = "name${bundleParam.botUserId}"
                readonly = true
            }
        }
        p(classes = "field withicons") {
            input(type = InputType.text, name = "tgid${bundleParam.botUserId}", classes = "text-input") {
                value = bundleParam.telegramId.toString()
                title = "ID в telegram"
//                id = "tgid${bundleParam.botUserId}"
                readonly = true
            }
        }
        p(classes = "icons") {
            onClick = "editButtonPress(this);"
//            onClick = "editButtonPress(${bundleParam.botUserId});"
            img(classes = "resize", alt = "EDIT", src = "png/pencil.png") {
//                id = "edit${bundleParam.botUserId}"
            }
        }
        p(classes = "icons") {
            img(classes = "resize", alt = "CHECK", src = "png/arrow_circle.png") {
//                id = "check${bundleParam.botUserId}"
            }
        }
        p(classes = "icons") {
            onClick = "deleteButtonPress(this);"
//            onClick = "deleteButtonPress(${bundleParam.botUserId});"
            img(classes = "resize", alt = "DELETE", src = "png/trash.png") {
//                id = "delete${bundleParam.botUserId}"
            }
        }
        hiddenInput {
            name = "id${bundleParam.botUserId}"
            value = "${bundleParam.botUserId}"
        }

        style { +"#template {display: none;}" }

    }
}