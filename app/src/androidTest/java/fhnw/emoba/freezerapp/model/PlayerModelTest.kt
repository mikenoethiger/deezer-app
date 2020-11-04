package fhnw.emoba.freezerapp.model

import fhnw.emoba.freezerapp.data.Track
import org.junit.Test

class PlayerModelTest {
    private val track1 = Track("{\"id\":1,\"readable\":false,\"title\":\"track1\",\"title_short\":\"\",\"title_version\":\"\",\"link\":\"\",\"duration\":0,\"rank\":0,\"explicit_lyrics\":false,\"explicit_content_lyrics\":0,\"explicit_content_cover\":0,\"preview\":\"https://cdns-preview-b.dzcdn.net/stream/c-bdab5f5d846a91f14a01b75731dbc22a-7.mp3\",\"md5_image\":\"\",\"artist\":{\"id\":-1,\"name\":\"\",\"link\":\"\",\"picture\":\"\",\"picture_small\":\"\",\"picture_medium\":\"\",\"picture_big\":\"\",\"picture_xl\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"album\":{\"id\":-1,\"title\":\"\",\"cover\":\"\",\"cover_small\":\"\",\"cover_medium\":\"\",\"cover_big\":\"\",\"cover_xl\":\"\",\"md5_image\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"type\":\"\"}")
    private val track2 = Track("{\"id\":2,\"readable\":false,\"title\":\"track2\",\"title_short\":\"\",\"title_version\":\"\",\"link\":\"\",\"duration\":0,\"rank\":0,\"explicit_lyrics\":false,\"explicit_content_lyrics\":0,\"explicit_content_cover\":0,\"preview\":\"https://cdns-preview-7.dzcdn.net/stream/c-7d29f91f6875494c4104a0c436581293-9.mp3\",\"md5_image\":\"\",\"artist\":{\"id\":-1,\"name\":\"\",\"link\":\"\",\"picture\":\"\",\"picture_small\":\"\",\"picture_medium\":\"\",\"picture_big\":\"\",\"picture_xl\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"album\":{\"id\":-1,\"title\":\"\",\"cover\":\"\",\"cover_small\":\"\",\"cover_medium\":\"\",\"cover_big\":\"\",\"cover_xl\":\"\",\"md5_image\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"type\":\"\"}")
    private val track3 = Track("{\"id\":3,\"readable\":false,\"title\":\"track3\",\"title_short\":\"\",\"title_version\":\"\",\"link\":\"\",\"duration\":0,\"rank\":0,\"explicit_lyrics\":false,\"explicit_content_lyrics\":0,\"explicit_content_cover\":0,\"preview\":\"https://cdns-preview-8.dzcdn.net/stream/c-853d19a12a694ccc74b2501acd802500-6.mp3\",\"md5_image\":\"\",\"artist\":{\"id\":-1,\"name\":\"\",\"link\":\"\",\"picture\":\"\",\"picture_small\":\"\",\"picture_medium\":\"\",\"picture_big\":\"\",\"picture_xl\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"album\":{\"id\":-1,\"title\":\"\",\"cover\":\"\",\"cover_small\":\"\",\"cover_medium\":\"\",\"cover_big\":\"\",\"cover_xl\":\"\",\"md5_image\":\"\",\"tracklist\":\"\",\"type\":\"\"},\"type\":\"\"}")

    private fun createPlayerModel(): PlayerModel {
        return PlayerModel
    }

    @Test
    fun testPlayer() {
        // given
        val player = createPlayerModel()
        val trackList = listOf(track1, track2, track3)
        val trackListName = "Hits"
        // when setting track
        player.setTrack(track1, trackList, trackListName)
        // then
        assert(player.trackListName() == trackListName)
        assert(!player.currentlyPlaying)
        assert(player.currentMillis == 0)
        assert(player.track() == track1)
        // when next track
        player.nextTrack()
        // then
        assert(!player.currentlyPlaying)
        val title = player.track().title
        assert(player.track() == track2)
        // when previous track
        player.previousTrack()
        // then
        assert(!player.currentlyPlaying)
        assert(player.track() == track1)
        // when playing
        player.play()
        // when
        assert(player.currentlyPlaying)
        assert(player.track() == track1)
    }
}