package fhnw.emoba.freezerapp.data.impl

import androidx.datastore.preferences.createDataStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalStorageServiceTest {

    private fun createStorageService(): LocalStorageService {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataStore = appContext.createDataStore(name = "unittest")
        return LocalStorageService(dataStore)
    }

    @Test
    fun testReadWriteFavorites() {
        // given
        val storageService = createStorageService()
        val trackIDs = listOf(1,2,3,4,5)
        // when
        val writeJob = GlobalScope.launch {
            storageService.writeFavoriteTracks(trackIDs)
        }
        // then
        GlobalScope.launch {
            writeJob.join()
            val trackIDsFlow = storageService.readFavoriteTracks()
            trackIDsFlow.collect { IDs ->
                assert(trackIDs == IDs)
            }
        }
    }

    @Test
    fun testReadWriteEmptyFavorites() {
        // given
        val storageService = createStorageService()
        // when
        val writeJob = GlobalScope.launch {
            storageService.writeFavoriteTracks(emptyList())
        }
        // then
        GlobalScope.launch {
            writeJob.join()
            val trackIDsFlow = storageService.readFavoriteTracks()
            trackIDsFlow.collect { IDs ->
                assert(IDs.isEmpty())
            }
        }
    }

    @Test
    fun testReadWriteSearchHistory() {
        // given
        val storageService = createStorageService()
        val searchTerms = listOf("term1", "term2", "term3")
        // when
        val writeJob = GlobalScope.launch {
            storageService.writeSearchHistory(searchTerms)
        }
        // then
        GlobalScope.launch {
            writeJob.join()
            val searchTermsFlow = storageService.readSearchHistory()
            searchTermsFlow.collect { result ->
                assert(searchTerms == result)
            }
        }
    }

    @Test
    fun testReadWriteEmptySearchHistory() {
        // given
        val storageService = createStorageService()
        // when
        val writeJob = GlobalScope.launch {
            storageService.writeFavoriteTracks(emptyList())
        }
        // then
        GlobalScope.launch {
            writeJob.join()
            val searchTermsFlow = storageService.readFavoriteTracks()
            searchTermsFlow.collect { result ->
                assert(result.isEmpty())
            }
        }
    }
}