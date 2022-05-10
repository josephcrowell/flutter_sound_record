# flutter_sound_record_platform_interface

A common platform interface for the [`flutter_sound_record`][1] plugin.

This interface allows platform-specific implementations of the `flutter_sound_record`
plugin, as well as the plugin itself, to ensure they are supporting the
same interface.

# Usage

To implement a new platform-specific implementation of `flutter_sound_record`, extend
[`FlutterSoundRecordPlatform`][2] with an implementation that performs the
platform-specific behavior, and when you register your plugin, set the default
`FlutterSoundRecordPlatform` by calling
`FlutterSoundRecordPlatform.instance = MyFlutterSoundRecordPlatform()`.

# Note on breaking changes

Strongly prefer non-breaking changes (such as adding a method to the interface)
over breaking changes for this package.

See https://flutter.dev/go/platform-interface-breaking-changes for a discussion
on why a less-clean interface is preferable to a breaking change.

[1]: ../flutter_sound_record
[2]: lib/flutter_sound_record_platform_interface.dart
