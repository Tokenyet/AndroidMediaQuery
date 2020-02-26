import 'dart:async';

import 'package:flutter/services.dart';

class Song {
  final name;
  final path;
  const Song(this.name, this.path);
}

class AndroidMediaQuery {
  static const MethodChannel _channel =
      const MethodChannel('android_media_query');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<List<Song>> get songs async {
    print("pre get");
    final Map results = (await _channel.invokeMethod("media.get")) as Map;
    print("post get");
    final Map<String, String> songs = results.cast<String, String>();
    print("after cast");
    List<Song> lists = new List<Song>();
    for (var song in songs.entries) {
      lists.add(Song(song.key, song.value));
    }
    print("after list");
    return lists;
  }

  static Future<bool> get isPermissionGranted async {
    return await _channel.invokeMethod("media.check");
  }
}
