package com.dkfhui

import com.dkfhui.model.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("/content", "mycontent")

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

        get("/tasks") {
            val tasks = TaskRepository.allTasks()
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }

        get("/tasks/byPriority/{priority?}") {
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
    }

    install(StatusPages) {
        exception<IllegalStateException> {call, cause ->
            call.respondText("App in illegal state because: ${cause.message}")
        }
    }
}
