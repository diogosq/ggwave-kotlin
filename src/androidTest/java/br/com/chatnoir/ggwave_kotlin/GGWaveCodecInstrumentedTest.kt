package br.com.chatnoir.ggwave_kotlin

import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test


class GGWaveCodecInstrumentedTest {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testListenAndDecode() = runTest {
        val codec = GGWaveCodec.Builder().build()
        // This will listen for a short time, likely decode nothing
        val result = codec.listenAndDecode(1000)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(timeout = 5000)
    fun testEncodeAndPlay() = runTest {
        var exception: Exception? = null
        val codec = GGWaveCodec.Builder().build()
        // This will play audio, so we just check that it does not throw
        try {
            codec.encodeAndPlay("Test message")
        } catch (e: Exception) {
            exception = e
        }
        assertEquals(null, exception)
    }

    @Test
    fun testBuilder() {
        val codec = GGWaveCodec.Builder()
            .sampleRate(44100f)
            .sampleFormatInp(GGWaveSampleFormat.I16)
            .sampleFormatOut(GGWaveSampleFormat.I16)
            .samplesPerFrame(512)
            .protocolId(GGWaveProtocolId.AUDIBLE_NORMAL)
            .volume(10)
            .build()
        assertNotNull(codec)
    }

    @Test
    fun testEncode() = runBlocking {
        val codec = GGWaveCodec.Builder().build()
        val message = "Hello GGWave!"
        codec.encode(message) { waveform, durationMs ->
            // The waveform should not be empty and duration should be positive
            assert(waveform.isNotEmpty())
            assert(durationMs > 0)
        }
    }

    @Test
    fun testEncodeAndDecode() = runBlocking {
        val codec = GGWaveCodec.Builder().build()
        val message = "Hello GGWave!"
        var encodedWaveform: ByteArray = byteArrayOf()
        var encodedSize = 0
        codec.encode(message) { waveform, _ ->
            encodedWaveform = waveform
            encodedSize = waveform.size
        }
        codec.decode(encodedWaveform, encodedSize) { decoded ->
            assertEquals(message, decoded)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(timeout = 10000)
    fun testEncodePlayListenDecodeEndToEnd() = runTest {
        val mutex = Mutex()
        val codec = GGWaveCodec.Builder().build()
        val testMessage = "EndToEnd GGWave!"
        // Start listening in the background
        var decoded: String? = null

        val flow: Flow<String> = codec.startListeningFlow()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            flow.collect {
                decoded = it
                mutex.unlock()
            }
        }
        // Give the listener a moment to start
        delay(5000)
        mutex.lock()
        // Encode and play the message
        codec.encodeAndPlay(
            message = testMessage,
            playFinished = { }
        )
        mutex.withLock { }

        assertNotNull(decoded)
        assertTrue(decoded!!.contains(testMessage))
        // Stop listening
        codec.stopListeningFlow()

    }
}