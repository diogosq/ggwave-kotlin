package br.com.chatnoir.ggwave_kotlin

@Suppress("unused")
class GGWaveCodecException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
