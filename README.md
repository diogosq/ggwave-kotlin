# ggwave-kotlin

ggwave-kotlin is a Kotlin/Android library that provides a JNI wrapper for the [GGWave](https://github.com/ggerganov/ggwave) C++ library, enabling data-over-sound communication in Android applications. It allows you to encode and decode short messages using sound waves, supporting a variety of protocols (audible, ultrasound, etc.) and sample formats.

## Implementation
- Use `GGWaveWrapper` for direct JNI access to GGWave functions
- Use `GGWaveCodec` or `GGWaveAPI` for higher-level Kotlin APIs to transmit and receive messages
- See the source code and examples for details on parameterization and integration

## Usage Example

### 1. Creating the codec

```kotlin
val codec = GGWaveCodec.Builder()
    .sampleRate(48000f)
    .sampleFormatInp(GGWaveSampleFormat.I16)
    .sampleFormatOut(GGWaveSampleFormat.I16)
    .protocolId(GGWaveProtocolId.AUDIBLE_FAST)
    .volume(5)
    .build()
```

### 2. Transmit a message (encode and play audio)

```kotlin
// In a suspend function
codec.encodeAndPlay("Hello GGWave!")
```

### 3. Receive and decode a message (listen and decode at the end)

```kotlin
// In a suspend function, with audio recording permission
val message = codec.listenAndDecode(listeningTime = 5000) // listens for 5 seconds
println(message)
```

### 4. Decode an already recorded audio buffer

```kotlin
// waveform: ByteArray containing the captured audio
// waveFormSize: number of valid bytes in the buffer
codec.decode(waveform, waveFormSize) { message ->
    println(message)
}
```

### 5. Receive messages continuously (Flow)

```kotlin
codec.startListeningFlow().collect { message ->
    println("Received: $message")
}
// To stop:
codec.stopListeningFlow()
```

## Notes
- Always use the same sample rate, format, and protocol settings for both transmission and reception.
- The receive functions require audio recording permission.
- See the function documentation for more details and optional parameters.

## License
This library is distributed under the MIT License. See GGWave's original license for C++ core details.
