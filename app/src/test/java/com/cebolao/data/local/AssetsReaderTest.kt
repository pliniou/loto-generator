package com.cebolao.data.local

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class AssetsReaderTest {
    @kotlinx.serialization.Serializable
    data class RawContest(
        val id: Int,
        val date: String,
        val numbers: List<Int>,
        val secondDrawNumbers: List<Int>? = null,
        val teamNumber: Int? = null,
    )

    @Test
    fun `parses optional fields secondDrawNumbers and teamNumber`() {
        val path = Paths.get("src/test/resources/fixtures/dupla_sena_fixture.json")
        val jsonText = String(Files.readAllBytes(path))
        val json = Json { ignoreUnknownKeys = true }
        val list = json.decodeFromString<List<RawContest>>(jsonText)

        assertEquals(1, list.size)
        val raw = list[0]
        assertNotNull(raw.secondDrawNumbers)
        assertEquals(6, raw.secondDrawNumbers?.size)
    }

    @Test
    fun `parses wrapper schema with schemaVersion and contests array`() {
        val path = Paths.get("src/test/resources/fixtures/contests_wrapper.json")
        val jsonText = String(Files.readAllBytes(path))
        val json = Json { ignoreUnknownKeys = true }

        // Local wrapper model matching the optional wrapper used by AssetsReader
        @kotlinx.serialization.Serializable
        data class Wrapper(val schemaVersion: String? = null, val contests: List<RawContest>? = null)

        val wrapper = json.decodeFromString<Wrapper>(jsonText)
        assertEquals("1.0", wrapper.schemaVersion)
        assertNotNull(wrapper.contests)
        assertEquals(1, wrapper.contests?.size)
    }
}
