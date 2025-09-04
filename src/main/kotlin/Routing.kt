package com.dkfhui

import com.dkfhui.model.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
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
            get {
                call.respond(
                    TaskRepository.allTasks()
                )
            }

            get("/byName/{name?}") {
                val name = call.parameters["name"]

                if(name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val task = TaskRepository.taskByName(name)

                    if(task == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }

                    call.respond(task)
                }
                catch (ex: java.lang.IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
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

                    call.respond(tasks)
                }
                catch (ex: java.lang.IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post {
                try {
                    val task = call.receive<Task>()

                    TaskRepository.addTask(task)

                    call.respond(HttpStatusCode.Created)
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

            delete("/{name?}") {
                val name = call.parameters["name"]

                if(name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                val removed = TaskRepository.removeTask(name)

                if(!removed) {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }


    }

    install(StatusPages) {
        exception<IllegalStateException> {call, cause ->
            call.respondText("App in illegal state because: ${cause.message}")
        }
    }
}
