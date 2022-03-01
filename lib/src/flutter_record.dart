import 'dart:async';

import 'package:flutter_record_platform_interface/flutter_record_platform_interface.dart';

/// Audio recorder API
class FlutterRecord implements FlutterRecordPlatform {
  @override
  Future<void> start({
    String? path,
    AudioEncoder encoder = AudioEncoder.AAC,
    int bitRate = 128000,
    double samplingRate = 44100.0,
  }) {
    return FlutterRecordPlatform.instance.start(
      path: path,
      encoder: encoder,
      bitRate: bitRate,
      samplingRate: samplingRate,
    );
  }

  @override
  Future<String?> stop() {
    return FlutterRecordPlatform.instance.stop();
  }

  @override
  Future<void> pause() {
    return FlutterRecordPlatform.instance.pause();
  }

  @override
  Future<void> resume() {
    return FlutterRecordPlatform.instance.resume();
  }

  @override
  Future<bool> isRecording() {
    return FlutterRecordPlatform.instance.isRecording();
  }

  @override
  Future<bool> isPaused() async {
    return FlutterRecordPlatform.instance.isPaused();
  }

  @override
  Future<bool> hasPermission() async {
    return FlutterRecordPlatform.instance.hasPermission();
  }

  @override
  Future<void> dispose() async {
    return FlutterRecordPlatform.instance.dispose();
  }

  @override
  Future<Amplitude> getAmplitude() {
    return FlutterRecordPlatform.instance.getAmplitude();
  }
}
