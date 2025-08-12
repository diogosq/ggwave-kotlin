package br.com.chatnoir.ggwave_kotlin

import org.junit.Assert
import org.junit.Test

class GGWaveCodecExceptionTest {
    @Test
    fun testDefaultConstructor() {
        val exception = GGWaveCodecException()
        Assert.assertNull(exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun testMessageConstructor() {
        val message = "Test error message"
        val exception = GGWaveCodecException(message)
        Assert.assertEquals(message, exception.message)
    }

    @Test
    fun testMessageAndCauseConstructor() {
        val message = "Test error message"
        val cause: Throwable = RuntimeException("Cause")
        val exception = GGWaveCodecException(message, cause)
        Assert.assertEquals(message, exception.message)
        Assert.assertEquals(cause, exception.cause)
    }

    @Test
    fun testCauseConstructor() {
        val cause: Throwable = RuntimeException("Cause")
        val exception = GGWaveCodecException(cause)
        Assert.assertEquals(cause, exception.cause)
    }
}
