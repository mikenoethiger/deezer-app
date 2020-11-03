package fhnw.emoba.freezerapp.data.impl

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteDeezerServiceTest {
    @Test
    fun testSearch() {
        // when
        val searchResults = RemoteDeezerService.search("Eminem")
        // then
        assert(searchResults.isNotEmpty())
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
        assert(searchResults.isNotEmpty())
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
        albumCount.forEach{ assert(1 == it.value) }
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
        artistCount.forEach{ assert(1 == it.value) }
    }

    @Test
    fun testLoadAlbum() {
        // given
        val albumId = 302127
        // when
        val album = RemoteDeezerService.loadAlbum(albumId)
        // then
        assert("Discovery" == album.title)
    }

    @Test
    fun testLoadArtist() {
        // given
        val artistId = 27
        // when
        val artist = RemoteDeezerService.loadArtist(artistId)
        // then
        assert("Daft Punk" == artist.name)
    }

    @Test
    fun testLoadTrack() {
        // given
        val trackID = 3135556
        // when
        val track = RemoteDeezerService.loadTrack(trackID)
        // then
        assert("Harder, Better, Faster, Stronger" == track.title)
    }

    @Test
    fun loadRadios() {
        // when
        val radios = RemoteDeezerService.loadRadios()
        // then
        assert(radios.isNotEmpty())
    }
}