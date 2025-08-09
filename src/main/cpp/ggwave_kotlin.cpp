#include <jni.h>
#include "ggwave/ggwave.h"
#include <cstring>

extern "C" JNIEXPORT jobject JNICALL
Java_br_com_chatnoir_ggwave_1kotlin_GGWaveWrapper_getDefaultParameters(JNIEnv *env, jobject /* this */) {
    ggwave_Parameters params = ggwave_getDefaultParameters();
    jclass cls = env->FindClass("br/com/chatnoir/ggwave_kotlin/GGWaveParameters");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(IFFFIFBBI)V");
    jobject obj = env->NewObject(cls, ctor,
        params.payloadLength,
        params.sampleRateInp,
        params.sampleRateOut,
        params.sampleRate,
        params.samplesPerFrame,
        params.soundMarkerThreshold,
        (jbyte)params.sampleFormatInp, // cast to jbyte
        (jbyte)params.sampleFormatOut, // cast to jbyte
        params.operatingMode
    );
    return obj;
}

extern "C" JNIEXPORT jlong JNICALL
Java_br_com_chatnoir_ggwave_1kotlin_GGWaveWrapper_init(JNIEnv *env, jobject /* this */, jobject jparams) {
    jclass cls = env->GetObjectClass(jparams);
    jfieldID fid_payloadLength = env->GetFieldID(cls, "payloadLength", "I");
    jfieldID fid_sampleRateInp = env->GetFieldID(cls, "sampleRateInp", "F");
    jfieldID fid_sampleRateOut = env->GetFieldID(cls, "sampleRateOut", "F");
    jfieldID fid_sampleRate = env->GetFieldID(cls, "sampleRate", "F");
    jfieldID fid_samplesPerFrame = env->GetFieldID(cls, "samplesPerFrame", "I");
    jfieldID fid_soundMarkerThreshold = env->GetFieldID(cls, "soundMarkerThreshold", "F");
    jfieldID fid_sampleFormatInp = env->GetFieldID(cls, "sampleFormatInp", "B");
    jfieldID fid_sampleFormatOut = env->GetFieldID(cls, "sampleFormatOut", "B");
    jfieldID fid_operatingMode = env->GetFieldID(cls, "operatingMode", "I");
    ggwave_Parameters params;
    params.payloadLength = env->GetIntField(jparams, fid_payloadLength);
    params.sampleRateInp = env->GetFloatField(jparams, fid_sampleRateInp);
    params.sampleRateOut = env->GetFloatField(jparams, fid_sampleRateOut);
    params.sampleRate = env->GetFloatField(jparams, fid_sampleRate);
    params.samplesPerFrame = env->GetIntField(jparams, fid_samplesPerFrame);
    params.soundMarkerThreshold = env->GetFloatField(jparams, fid_soundMarkerThreshold);
    params.sampleFormatInp = (ggwave_SampleFormat)env->GetByteField(jparams, fid_sampleFormatInp);
    params.sampleFormatOut = (ggwave_SampleFormat)env->GetByteField(jparams, fid_sampleFormatOut);
    params.operatingMode = env->GetIntField(jparams, fid_operatingMode);
    ggwave_Instance inst = ggwave_init(params);
    return (jlong)inst;
}

extern "C" JNIEXPORT void JNICALL
Java_br_com_chatnoir_ggwave_1kotlin_GGWaveWrapper_free(JNIEnv *env, jobject /* this */, jlong instance) {
    ggwave_free((ggwave_Instance)instance);
}

extern "C" JNIEXPORT jint JNICALL
Java_br_com_chatnoir_ggwave_1kotlin_GGWaveWrapper_encode(JNIEnv *env, jobject /* this */, jlong instance, jbyteArray payload, jint payloadSize, jint protocolId, jint volume, jbyteArray waveform, jint query) {
    jbyte* payloadPtr = env->GetByteArrayElements(payload, NULL);
    jbyte* waveformPtr = env->GetByteArrayElements(waveform, NULL);
    int ret = ggwave_encode((ggwave_Instance)instance, payloadPtr, payloadSize, (ggwave_ProtocolId)protocolId, volume, waveformPtr, query);
    env->ReleaseByteArrayElements(payload, payloadPtr, JNI_ABORT);
    env->ReleaseByteArrayElements(waveform, waveformPtr, 0);
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_br_com_chatnoir_ggwave_1kotlin_GGWaveWrapper_decode(JNIEnv *env, jobject /* this */, jlong instance, jbyteArray waveform, jint waveformSize, jbyteArray payload) {
    jbyte* waveformPtr = env->GetByteArrayElements(waveform, NULL);
    jbyte* payloadPtr = env->GetByteArrayElements(payload, NULL);
    int ret = ggwave_decode((ggwave_Instance)instance, waveformPtr, waveformSize, payloadPtr);
    env->ReleaseByteArrayElements(waveform, waveformPtr, JNI_ABORT);
    env->ReleaseByteArrayElements(payload, payloadPtr, 0);
    return ret;
}
