package fhnw.emoba.freezerapp.data.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RemoteDeezerServiceTest {
    @Test
    fun testSearch() {
        // given
        val searchParameters = emptyMap<String, String>()
            .plus(Pair("artist", "Eminem"))
            .plus(Pair("album", "The Eminem Show"))
            .plus(Pair("track", "Sing For The Moment"))
        // when
        val searchResults = RemoteDeezerService.search(searchParameters)
        // then
        assertTrue(searchResults.isNotEmpty())
    }
}