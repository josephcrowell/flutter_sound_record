#import "FlutterSoundRecordPlugin.h"
#if __has_include(<flutter_sound_record/flutter_sound_record-Swift.h>)
#import <flutter_sound_record/flutter_sound_record-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_sound_record-Swift.h"
#endif

@implementation FlutterSoundRecordPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterSoundRecordPlugin registerWithRegistrar:registrar];
}
@end
