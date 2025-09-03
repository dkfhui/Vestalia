package com.dkfhui

import io.ktor.http.ContentType
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
    }

    install(StatusPages) {
        exception<IllegalStateException> {call, cause ->
            call.respondText("App in illegal state because: ${cause.message}")
        }
    }
}
