package br.com.chatnoir.ggwave_kotlin

object GGWaveWrapper {
    init {
        System.loadLibrary("ggwave_jni")
    }

    external fun getDefaultParameters(): GGWaveParameters
    external fun init(params: GGWaveParameters): Long
    external fun free(instance: Long)
    external fun encode(
        instance: Long,
        payload: ByteArray,
        payloadSize: Int,
        protocolId: Int,
        volume: Int,
        waveform: ByteArray,
        query: Int
    ): Int
    external fun decode(
        instance: Long,
        waveform: ByteArray,
        waveformSize: Int,
        payload: ByteArray
    ): Int
}

@Suppress("unused")
object GGWaveSampleFormat {
    const val UNDEFINED: Byte = 0
    const val U8: Byte = 1
    const val I8: Byte = 2
    const val U16: Byte = 3
    const val I16: Byte = 4 // PCM 16-bit
    const val F32: Byte = 5
}

@Suppress("unused")
object GGWaveProtocolId {
    const val AUDIBLE_NORMAL = 0
    const val AUDIBLE_FAST = 1
    const val AUDIBLE_FASTEST = 2
    const val ULTRASOUND_NORMAL = 3
    const val ULTRASOUND_FAST = 4
    const val ULTRASOUND_FASTEST = 5
    const val DT_NORMAL = 6
    const val DT_FAST = 7
    const val DT_FASTEST = 8
    const val MT_NORMAL = 9
    const val MT_FAST = 10
    const val MT_FASTEST = 11
    const val CUSTOM_0 = 12
    const val CUSTOM_1 = 13
}

/**
 *  query
 *  if == 0, encode data in to waveformBuffer, returns number of bytes
 *  if != 1, return waveform size in samples
 *  if != 0, do not perform encoding.
 *  if == 1, return waveform size in bytes
 */
object GGWaveQuery{
    const val NONE = 0
    const val WAVEFORM_SIZE = 1
}

