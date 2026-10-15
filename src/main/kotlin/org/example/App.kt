package org.example

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL
import java.nio.charset.StandardCharsets

internal fun messagePayload(): String =
    """{"service":"kotlin1-service-1","message":"Kotlin1 entry service","sharedLibrary":{"name":"kot-lib-1","version":"${Kotlib1.version()}"}}"""

private fun reply(ex: HttpExchange, code: Int, body: String) {
    val bytes = body.toByteArray(StandardCharsets.UTF_8)
    ex.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
    ex.sendResponseHeaders(code, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

private fun get(url: String): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.connectTimeout = 2000
    connection.readTimeout = 2000
    return try {
        val stream =
            if (connection.responseCode < 400) connection.inputStream
            else connection.errorStream
        stream.bufferedReader().use { it.readText() }
    } finally {
        connection.disconnect()
    }
}

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8081
    val service2 = System.getenv("SERVICE2_URL") ?: "http://127.0.0.1:8082"
    val service3 = System.getenv("SERVICE3_URL") ?: "http://127.0.0.1:8083"
    val pythonService =
        System.getenv("KOTLIN1_PYTHON_URL") ?: "http://127.0.0.1:8087"

    val server = HttpServer.create(InetSocketAddress("0.0.0.0", port), 0)

    server.createContext("/health") { exchange ->
        reply(exchange, 200, """{"status":"ok","service":"kotlin1-service-1"}""")
    }

    server.createContext("/api/message") { exchange ->
        reply(exchange, 200, messagePayload())
    }

    server.createContext("/api/summary") { exchange ->
        try {
            val response2 = get("$service2/api/message")
            val response3 = get("$service3/api/message")
            val responsePython = get("$pythonService/api/message")

            reply(
                exchange,
                200,
                """{"application":"Kotlin1","status":"ok","services":[{"name":"kotlin1-service-2","response":$response2},{"name":"kotlin1-service-3","response":$response3},{"name":"kotlin1-python-service","response":$responsePython}]}"""
            )
        } catch (ex: Exception) {
            reply(
                exchange,
                503,
                """{"application":"Kotlin1","status":"degraded","error":"A downstream service is unavailable","details":"${ex.javaClass.simpleName}"}"""
            )
        }
    }

    server.start()
    println("kotlin1-service-1 listening on http://localhost:$port")
    println("Kotlin1 summary calls services 2, 3, and Python service at $pythonService")
}
