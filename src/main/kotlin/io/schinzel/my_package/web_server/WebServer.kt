package io.schinzel.my_package.web_server

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location

fun main() {
    WebServer(5555)
}

/**
 * http://127.0.0.1:5555/
 * http://127.0.0.1:5555/index.html
 * java -jar myJar.jar
 */
class WebServer(port: Int) {
    init {

        Javalin.create { config ->
            config.staticFiles.add("/site", Location.CLASSPATH)
        }
            .get("/") { ctx -> ctx.result("Hello Public World") }
            .start(port)
    }
}