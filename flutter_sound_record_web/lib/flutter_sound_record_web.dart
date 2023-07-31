import 'dart:async';
import 'dart:html' as html;

import 'package:flutter/foundation.dart';
import 'package:flutter_sound_record_platform_interface/flutter_sound_record_platform_interface.dart';
import 'package:flutter_web_plugins/flutter_web_plugins.dart';

class FlutterSoundRecordPluginWeb extends FlutterSoundRecordPlatform {
  static void registerWith(Registrar registrar) {
    FlutterSoundRecordPlatform.instance = FlutterSoundRecordPluginWeb();
  }

  // Media recorder object
  html.MediaRecorder? _mediaRecorder;
  // Audio data
  List<html.Blob> _chunks = <html.Blob>[];
  // Completer to get data & stop events before `stop()` method ends
  Completer<String>? _onStopCompleter;

  @override
  Future<void> dispose() async {
    _mediaRecorder?.stop();
    _resetMediaRecorder();
  }

  @override
  Future<bool> hasPermission() async {
    final html.MediaDevices? mediaDevices = html.window.navigator.mediaDevices;
    if (mediaDevices == null) {
      return false;
    }

    try {
      await mediaDevices.getUserMedia(<dynamic, dynamic>{'audio': true});
      return true;
    } catch (_) {
      return false;
    }
  }

  @override
  Future<bool> isPaused() async {
    return _mediaRecorder?.state == 'paused';
  }

  @override
  Future<bool> isRecording() async {
    return _mediaRecorder?.state == 'recording';
  }

  @override
  Future<void> pause() async {
    if (kDebugMode) {
      print('Recording paused');
    }

    _mediaRecorder?.pause();
  }

  @override
  Future<void> resume() async {
    if (kDebugMode) {
      print('Recording resumed');
    }
    _mediaRecorder?.resume();
  }

  // TODO apply correct audio encoder config
  // Try to get it from html.MediaRecorder.isTypeSupported(mimeType) ?
  @override
  Future<void> start({
    String? path,
    AudioEncoder encoder = AudioEncoder.AAC,
    int bitRate = 128000,
    double samplingRate = 44100.0,
  }) async {
    _mediaRecorder?.stop();
    _resetMediaRecorder();

    try {
      final html.MediaStream? stream = await html.window.navigator.mediaDevices?.getUserMedia(<dynamic, dynamic>{
        'audio': true,
        'audioBitsPerSecond': bitRate,
        'bitsPerSecond': bitRate,
      });
      if (stream != null) {
        _onStart(stream);
      } else {
        if (kDebugMode) {
          print('Audio recording not supported.');
        }
      }
    } catch (error, stack) {
      _onError(error, stack);
    }
  }

  @override
  Future<String?> stop() async {
    _onStopCompleter = Completer<String>();

    _mediaRecorder?.stop();

    return _onStopCompleter?.future;
  }

  void _onStart(html.MediaStream stream) {
    if (kDebugMode) {
      print('Start recording');
    }

    _mediaRecorder = html.MediaRecorder(stream);
    _mediaRecorder?.addEventListener('dataavailable', _onDataAvailable);
    _mediaRecorder?.addEventListener('stop', _onStop);
    _mediaRecorder?.start();
  }

  // ignore: always_specify_types
  void _onError(error, StackTrace trace) {
    if (kDebugMode) {
      print(error);
    }
  }

  void _onDataAvailable(html.Event event) {
    if (event is html.BlobEvent && event.data != null) {
      _chunks.add(event.data!);
    }
  }

  void _onStop(html.Event event) {
    if (kDebugMode) {
      print('Stop recording');
    }

    String? audioUrl;

    if (_chunks.isNotEmpty) {
      final html.Blob blob = html.Blob(_chunks);
      audioUrl = html.Url.createObjectUrl(blob);
    }

    _resetMediaRecorder();

    _onStopCompleter?.complete(audioUrl);
  }

  void _resetMediaRecorder() {
    _mediaRecorder?.removeEventListener('dataavailable', _onDataAvailable);
    _mediaRecorder?.removeEventListener('stop', _onStop);
    _mediaRecorder = null;

    _chunks = <html.Blob>[];
  }

  @override
  Future<Amplitude> getAmplitude() async {
    // TODO how to check amplitude values on web?
    return Amplitude(current: -160, max: -160);
  }
}
