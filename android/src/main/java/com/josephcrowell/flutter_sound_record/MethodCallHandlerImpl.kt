package com.josephcrowell.flutter_sound_record

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException

class MethodCallHandlerImpl internal constructor(private val activity: Activity) :
    MethodCallHandler, RequestPermissionsResultListener {
    private val recorder = Recorder(activity)
    private var pendingPermResult: MethodChannel.Result? = null
    fun close() {
        recorder.close()
        pendingPermResult = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "start" -> {
                var path = call.argument<Any>("path") as String?
                if (path == null) {
                    val outputDir = activity.cacheDir
                    val outputFile: File?
                    try {
                        outputFile = File.createTempFile("audio", ".m4a", outputDir)
                        path = outputFile.path
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                recorder.start(
                    path!!,
                    call.argument<Any>("encoder") as Int,
                    call.argument<Any>("bitRate") as Int,
                    call.argument<Any>("samplingRate") as Double,
                    result
                )
            }
            "stop" -> recorder.stop(result)
            "pause" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.pause(result)
            }
            "resume" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.resume(result)
            }
            "isPaused" -> recorder.isPaused(result)
            "isRecording" -> recorder.isRecording(result)
            "hasPermission" -> hasPermission(result)
            "getAmplitude" -> recorder.getAmplitude(result)
            "dispose" -> {
                close()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (pendingPermResult != null) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pendingPermResult!!.success(true)
                } else {
                    pendingPermResult!!.error("-2", "Permission denied", null)
                }
                pendingPermResult = null
                return true
            }
        }
        return false
    }

    private fun hasPermission(result: MethodChannel.Result) {
        if (!isPermissionGranted) {
            pendingPermResult = result
            askForPermission()
        } else {
            result.success(true)
        }
    }

    private val isPermissionGranted: Boolean
        get() {
            val result =
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            return result == PackageManager.PERMISSION_GRANTED
        }

    private fun askForPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_REQUEST_CODE
        )
    }

    companion object {
        private const val RECORD_AUDIO_REQUEST_CODE = 1001
    }
}