package com.dkfhui

import com.dkfhui.model.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("/content", "mycontent")
        staticResources("/task-ui", "task-ui")

        get("/") {
            call.respondText("Hello World!")
        }

        get("/test1") {
            val text = "<h1>Hello From Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }

        get("/errortest") {
            throw IllegalStateException("This is a test page")
        }

        route("/tasks") {
            get() {
                val tasks = TaskRepository.allTasks()
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = tasks.tasksAsTable()
                )
            }

            get("/byPriority/{priority?}") {
                val priorityString = call.parameters["priority"]

                if (priorityString == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityString)
                    val tasks = TaskRepository.tasksByPriority(priority)

                    if(tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }

                    call.respondText(
                        contentType = ContentType.parse("text/html"),
                        text = tasks.tasksAsTable()
                    )
                }
                catch (ex: java.lang.IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post {
                val formContent = call.receiveParameters()

                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )

                if(params.toList().any {it.isBlank()}) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val priority = Priority.valueOf(params.third)

                    TaskRepository.addTask(
                        Task(
                            name = params.first,
                            description = params.second,
                            priority = priority
                        )
                    )

                    call.respond(HttpStatusCode.NoContent)
                }
                catch (ex: java.lang.IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
                catch( ex: IllegalStateException) {
                    call.respondText(
                        "Cannot add task because: ${ex.message}",
                        status = HttpStatusCode.BadRequest
                    )
                }
            }
        }


    }

    install(StatusPages) {
        exception<IllegalStateException> {call, cause ->
            call.respondText("App in illegal state because: ${cause.message}")
        }
    }
}
