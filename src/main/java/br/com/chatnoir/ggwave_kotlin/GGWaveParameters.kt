package br.com.chatnoir.ggwave_kotlin

data class GGWaveParameters(
    val payloadLength: Int,
    val sampleRateInp: Float,
    val sampleRateOut: Float,
    val sampleRate: Float,
    val samplesPerFrame: Int,
    val soundMarkerThreshold: Float,
    val sampleFormatInp: Byte, // changed from Int to Byte
    val sampleFormatOut: Byte, // changed from Int to Byte
    val operatingMode: Int
)
