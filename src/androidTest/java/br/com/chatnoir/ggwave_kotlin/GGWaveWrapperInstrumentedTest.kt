package br.com.chatnoir.ggwave_kotlin

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GGWaveWrapperInstrumentedTest {
    @Test
    fun testLibraryLoad() {
        // The static initializer should load the library without throwing
        try {
            GGWaveWrapper.getDefaultParameters()
            assertTrue(true)
        } catch (e: UnsatisfiedLinkError) {
            fail("Native library failed to load: ${e.message}")
        } catch (e: Exception) {
            fail("Unexpected exception: ${e.message}")
        }
    }

    @Test
    fun testInitAndFree() {
        val params = try {
            GGWaveWrapper.getDefaultParameters()
        } catch (e: Throwable) {
            fail("Failed to get default parameters: ${e.message}")
            return
        }
        val instance = GGWaveWrapper.init(params)
        val instance2 = GGWaveWrapper.init(params)
        val instance3 = GGWaveWrapper.init(params)
        assertTrue(instance == 0L)
        assertTrue(instance2 == 1L)
        assertTrue(instance3 == 2L)
        GGWaveWrapper.free(instance)
        GGWaveWrapper.free(instance2)
        GGWaveWrapper.free(instance3)
    }

    @Test
    fun testEncode() {
        val (instance, payload, payloadSize, protocolId, volume) = prepareEncodeTest()
        val query = GGWaveQuery.WAVEFORM_SIZE
        val dummyWaveform = ByteArray(4096)
        val waveformSize = GGWaveWrapper.encode(
            instance,
            payload,
            payloadSize,
            protocolId,
            volume,
            dummyWaveform,
            query
        )
        assertTrue(waveformSize > 0)
        val waveform = ByteArray(waveformSize)
        val encodedSize = GGWaveWrapper.encode(
            instance,
            payload,
            payloadSize,
            protocolId,
            volume,
            waveform,
            GGWaveQuery.NONE
        )
        assertTrue(encodedSize > 0)
        GGWaveWrapper.free(instance)
    }

    @Test
    fun testDecode() {
        val (instance, payload, payloadSize, protocolId, volume) = prepareEncodeTest()
        val query = GGWaveQuery.WAVEFORM_SIZE
        val dummyWaveform = ByteArray(4096)
        val waveformSize = GGWaveWrapper.encode(
            instance,
            payload,
            payloadSize,
            protocolId,
            volume,
            dummyWaveform,
            query
        )
        val waveform = ByteArray(waveformSize)
        val encodedSize = GGWaveWrapper.encode(
            instance,
            payload,
            payloadSize,
            protocolId,
            volume,
            waveform,
            GGWaveQuery.NONE
        )
        val decodedPayload = ByteArray(payloadSize)
        val decodedSize = GGWaveWrapper.decode(
            instance,
            waveform,
            encodedSize,
            decodedPayload
        )
        assertTrue(decodedSize > 0)
        val decodedMessage = decodedPayload.decodeToString().trimEnd('\u0000')
        assertTrue(decodedMessage.contains("Hello GGWave!"))
        GGWaveWrapper.free(instance)
    }

    private fun prepareEncodeTest(): EncodeTestParams {
        val params = try {
            GGWaveWrapper.getDefaultParameters()
        } catch (e: Throwable) {
            fail("Failed to get default parameters: ${e.message}")
            throw e
        }
        val instance = GGWaveWrapper.init(params)
        assertTrue(instance == 0L)
        val message = "Hello GGWave!"
        val payload = message.toByteArray(Charsets.UTF_8)
        val payloadSize = payload.size
        val protocolId = GGWaveProtocolId.AUDIBLE_NORMAL
        val volume = 100
        return EncodeTestParams(instance, payload, payloadSize, protocolId, volume)
    }

    private data class EncodeTestParams(
        val instance: Long,
        val payload: ByteArray,
        val payloadSize: Int,
        val protocolId: Int,
        val volume: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EncodeTestParams

            if (instance != other.instance) return false
            if (payloadSize != other.payloadSize) return false
            if (protocolId != other.protocolId) return false
            if (volume != other.volume) return false
            if (!payload.contentEquals(other.payload)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = instance.hashCode()
            result = 31 * result + payloadSize
            result = 31 * result + protocolId
            result = 31 * result + volume
            result = 31 * result + payload.contentHashCode()
            return result
        }
    }
}
