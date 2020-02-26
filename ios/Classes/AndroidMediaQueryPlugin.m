#import "AndroidMediaQueryPlugin.h"
#import <android_media_query/android_media_query-Swift.h>

@implementation AndroidMediaQueryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidMediaQueryPlugin registerWithRegistrar:registrar];
}
@end
