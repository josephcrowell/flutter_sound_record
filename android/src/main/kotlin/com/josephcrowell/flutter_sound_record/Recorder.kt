package com.josephcrowell.flutter_sound_record

import android.app.Activity
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.HashMap
import kotlin.math.log10

internal class Recorder(private val activity: Activity) {
    private var isRecording = false
    private var isPaused = false
    private var recorder: MediaRecorder? = null
    private var path: String? = null
    private var maxAmplitude = -160.0

    fun start(
        path: String,
        encoder: Int,
        bitRate: Int,
        samplingRate: Double,
        result: MethodChannel.Result
    ) {
        stopRecording()
        Log.d(LOG_TAG, "Start recording")
        this.path = path
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(activity.baseContext)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setAudioEncodingBitRate(bitRate)
        recorder!!.setAudioSamplingRate(samplingRate.toInt())
        recorder!!.setOutputFormat(getOutputFormat(encoder))
        // must be set after output format
        recorder!!.setAudioEncoder(getEncoder(encoder))
        recorder!!.setOutputFile(path)
        try {
            recorder!!.prepare()
            recorder!!.start()
            isRecording = true
            isPaused = false
            result.success(null)
        } catch (e: Exception) {
            recorder!!.release()
            recorder = null
            result.error("-1", "Start recording failure", e.message)
        }
    }

    fun stop(result: MethodChannel.Result) {
        stopRecording()
        result.success(path)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun pause(result: MethodChannel.Result) {
        pauseRecording()
        result.success(null)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun resume(result: MethodChannel.Result) {
        resumeRecording()
        result.success(null)
    }

    fun isRecording(result: MethodChannel.Result) {
        result.success(isRecording)
    }

    fun isPaused(result: MethodChannel.Result) {
        result.success(isPaused)
    }

    fun getAmplitude(result: MethodChannel.Result) {
        val amp: MutableMap<String, Any> = HashMap()
        var current = -160.0
        if (isRecording) {
            current = 20 * log10(recorder!!.maxAmplitude / 32768.0)
            if (current > maxAmplitude) {
                maxAmplitude = current
            }
        }
        amp["current"] = current
        amp["max"] = maxAmplitude
        result.success(amp)
    }

    fun close() {
        stopRecording()
    }

    private fun stopRecording() {
        if (recorder != null) {
            try {
                if (isRecording || isPaused) {
                    Log.d(LOG_TAG, "Stop recording")
                    recorder!!.stop()
                }
            } catch (ex: IllegalStateException) {
                // Mute this exception since 'isRecording' can't be 100% sure
            } finally {
                recorder!!.reset()
                recorder!!.release()
                recorder = null
            }
        }
        isRecording = false
        isPaused = false
        maxAmplitude = -160.0
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if (recorder != null) {
            try {
                if (isRecording) {
                    Log.d(LOG_TAG, "Pause recording")
                    recorder!!.pause()
                    isPaused = true
                }
            } catch (ex: IllegalStateException) {
                Log.d(
                    LOG_TAG, """
     Did you call pause() before before start() or after stop()?
     ${ex.message}
     """.trimIndent()
                )
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun resumeRecording() {
        if (recorder != null) {
            try {
                if (isPaused) {
                    Log.d(LOG_TAG, "Resume recording")
                    recorder!!.resume()
                    isPaused = false
                }
            } catch (ex: IllegalStateException) {
                Log.d(
                    LOG_TAG, """
     Did you call resume() before before start() or after stop()?
     ${ex.message}
     """.trimIndent()
                )
            }
        }
    }

    private fun getOutputFormat(encoder: Int): Int {
        return if (encoder == 3 || encoder == 4) {
            MediaRecorder.OutputFormat.THREE_GPP
        } else MediaRecorder.OutputFormat.MPEG_4
    }

    // https://developer.android.com/reference/android/media/MediaRecorder.AudioEncoder
    private fun getEncoder(encoder: Int): Int {
        return when (encoder) {
            1 -> MediaRecorder.AudioEncoder.AAC_ELD
            2 -> MediaRecorder.AudioEncoder.HE_AAC
            3 -> MediaRecorder.AudioEncoder.AMR_NB
            4 -> MediaRecorder.AudioEncoder.AMR_WB
            5 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return MediaRecorder.AudioEncoder.OPUS
                } else {
                    Log.d(
                        LOG_TAG,
                        "OPUS codec is available starting from API 29.\nFalling back to AAC"
                    )
                }
                MediaRecorder.AudioEncoder.AAC
            }
            else -> MediaRecorder.AudioEncoder.AAC
        }
    }

    companion object {
        private const val LOG_TAG = "Record"
    }
}