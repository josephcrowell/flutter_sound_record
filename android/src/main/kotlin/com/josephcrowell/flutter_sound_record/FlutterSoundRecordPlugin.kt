package com.josephcrowell.flutter_sound_record

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger

/**
 * RecordPlugin
 */
class FlutterSoundRecordPlugin : FlutterPlugin, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native
    /// Android
    private var channel: MethodChannel? = null

    /// Our call handler
    private var handler: MethodCallHandlerImpl? = null
    private var pluginBinding: FlutterPluginBinding? = null
    private var activityBinding: ActivityPluginBinding? = null

    /////////////////////////////////////////////////////////////////////////////
    /// FlutterPlugin
    /////////////////////////////////////////////////////////////////////////////
    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        pluginBinding = binding
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        pluginBinding = null
    }

    /// END FlutterPlugin
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /// ActivityAware
    /////////////////////////////////////////////////////////////////////////////
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        startPlugin(pluginBinding!!.binaryMessenger, binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        stopPlugin()
    }

    private fun startPlugin(messenger: BinaryMessenger, binding: ActivityPluginBinding) {
        handler = MethodCallHandlerImpl(binding.activity)
        channel = MethodChannel(messenger, "com.josephcrowell.flutter_sound_record")
        channel!!.setMethodCallHandler(handler)
        binding.addRequestPermissionsResultListener(handler!!)
    }

    private fun stopPlugin() {
        activityBinding!!.removeRequestPermissionsResultListener(handler!!)
        activityBinding = null
        channel!!.setMethodCallHandler(null)
        handler!!.close()
        handler = null
        channel = null
    } /// END ActivityAware
    /////////////////////////////////////////////////////////////////////////////
}