import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_media_query/android_media_query.dart';

void main() {
  const MethodChannel channel = MethodChannel('android_media_query');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AndroidMediaQuery.platformVersion, '42');
  });
}
