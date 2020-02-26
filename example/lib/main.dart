import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:android_media_query/android_media_query.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  List<Song> _songs;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    initMediaQuery();
  }
  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initMediaQuery() async {
    var songs;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      songs = await AndroidMediaQuery.songs;
      print(songs);
    } on PlatformException {
      //platformVersion = 'Failed to get platform version.';
      print("get songs error");
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _songs = songs;
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await AndroidMediaQuery.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child:
          ListView(
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              //ListTile(title: Text(_songs != null ? _songs[0].name : ""), subtitle: Text(_songs != null ? _songs[0].path : "",)),
              FlatButton(child: Text("Hi"), onPressed: () async {
                if(await AndroidMediaQuery.isPermissionGranted)
                  _songs = await AndroidMediaQuery.songs;
                  for(var song in _songs)
                    print(song.name);
              },)
            ],
          )
        ),
      ),
    );
  }
}
