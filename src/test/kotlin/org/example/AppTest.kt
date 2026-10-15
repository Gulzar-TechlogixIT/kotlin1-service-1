package org.example

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class AppTest {
    @Test
    fun messagePayloadReportsKotLib1Version() {
        val payload = messagePayload()
        assertContains(payload, "\"name\":\"kot-lib-1\"")
        assertContains(payload, "\"version\":\"0.1.0\"")
        assertEquals("0.1.0", Kotlib1.version())
    }
}
