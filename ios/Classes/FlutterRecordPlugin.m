#import "FlutterRecordPlugin.h"
#if __has_include(<flutter_record/flutter_record-Swift.h>)
#import <flutter_record/flutter_record-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_record-Swift.h"
#endif

@implementation FlutterRecordPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterRecordPlugin registerWithRegistrar:registrar];
}
@end
