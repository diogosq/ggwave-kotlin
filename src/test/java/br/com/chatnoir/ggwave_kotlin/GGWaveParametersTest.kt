package br.com.chatnoir.ggwave_kotlin

import org.junit.Assert.assertEquals
import org.junit.Test

class GGWaveParametersTest {
    @Test
    fun testGGWaveParametersCreation() {
        val params = GGWaveParameters(
            payloadLength = 10,
            sampleRateInp = 48000f,
            sampleRateOut = 48000f,
            sampleRate = 48000f,
            samplesPerFrame = 16384,
            soundMarkerThreshold = 0.5f,
            sampleFormatInp = 1,
            sampleFormatOut = 1,
            operatingMode = 0
        )
        assertEquals(10, params.payloadLength)
        assertEquals(48000f, params.sampleRateInp)
        assertEquals(48000f, params.sampleRateOut)
        assertEquals(48000f, params.sampleRate)
        assertEquals(16384, params.samplesPerFrame)
        assertEquals(0.5f, params.soundMarkerThreshold)
        assertEquals(1.toByte(), params.sampleFormatInp)
        assertEquals(1.toByte(), params.sampleFormatOut)
        assertEquals(0, params.operatingMode)
    }

    @Test
    fun testGGWaveParametersEquality() {
        val params1 = GGWaveParameters(1, 2f, 3f, 4f, 5, 6f, 7, 8, 9)
        val params2 = GGWaveParameters(1, 2f, 3f, 4f, 5, 6f, 7, 8, 9)
        assertEquals(params1, params2)
    }
}