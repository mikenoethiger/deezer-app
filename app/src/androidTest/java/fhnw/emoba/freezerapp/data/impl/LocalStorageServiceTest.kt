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
    fun testReadWrite() {
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
    fun testReadWriteEmpty() {
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
}