import 'dart:async';

import 'package:flutter_sound_record_platform_interface/flutter_sound_record_platform_interface.dart';

/// Audio recorder API
class FlutterSoundRecord implements FlutterSoundRecordPlatform {
  @override
  Future<void> start({
    String? path,
    AudioEncoder encoder = AudioEncoder.AAC,
    int bitRate = 128000,
    double samplingRate = 44100.0,
  }) {
    return FlutterSoundRecordPlatform.instance.start(
      path: path,
      encoder: encoder,
      bitRate: bitRate,
      samplingRate: samplingRate,
    );
  }

  @override
  Future<String?> stop() {
    return FlutterSoundRecordPlatform.instance.stop();
  }

  @override
  Future<void> pause() {
    return FlutterSoundRecordPlatform.instance.pause();
  }

  @override
  Future<void> resume() {
    return FlutterSoundRecordPlatform.instance.resume();
  }

  @override
  Future<bool> isRecording() {
    return FlutterSoundRecordPlatform.instance.isRecording();
  }

  @override
  Future<bool> isPaused() async {
    return FlutterSoundRecordPlatform.instance.isPaused();
  }

  @override
  Future<bool> hasPermission() async {
    return FlutterSoundRecordPlatform.instance.hasPermission();
  }

  @override
  Future<void> dispose() async {
    return FlutterSoundRecordPlatform.instance.dispose();
  }

  @override
  Future<Amplitude> getAmplitude() {
    return FlutterSoundRecordPlatform.instance.getAmplitude();
  }
}
