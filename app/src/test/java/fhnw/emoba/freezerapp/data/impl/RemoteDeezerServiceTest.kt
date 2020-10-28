package fhnw.emoba.freezerapp.data.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RemoteDeezerServiceTest {
    @Test
    fun testSearch() {
        // when
        val searchResults = RemoteDeezerService.search("Eminem")
        // then
        assertTrue(searchResults.isNotEmpty())
    }

    @Test
    fun testExtendedSearch() {
        // given
        val searchParameters = emptyMap<String, String>()
            .plus(Pair("artist", "Eminem"))
            .plus(Pair("album", "The Eminem Show"))
            .plus(Pair("track", "Sing For The Moment"))
        // when
        val searchResults = RemoteDeezerService.extendedSearch(searchParameters)
        // then
        assertTrue(searchResults.isNotEmpty())
    }

    @Test
    fun testUniqueAlbums() {
        // given
        val searchResults = RemoteDeezerService.search("Eminem")
        // when
        val albums = RemoteDeezerService.uniqueAlbums(searchResults)
        // then
        val albumCount = mutableMapOf<Int, Int>()
        albums.forEach{ albumCount.compute(it.id) { _, count -> count?.plus(1) ?: 1} }
        albumCount.forEach{ assertEquals(1, it.value) }
    }

    @Test
    fun testUniqueArtists() {
        // given
        val searchResults = RemoteDeezerService.search("Happy")
        // when
        val artists = RemoteDeezerService.uniqueArtists(searchResults)
        // then
        val artistCount = mutableMapOf<Int, Int>()
        artists.forEach{ artistCount.compute(it.id) { _, count -> count?.plus(1) ?: 1} }
        artistCount.forEach{ assertEquals(1, it.value) }
    }
}