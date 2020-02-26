package com.dowen.android_media_query

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Debug
import android.provider.MediaStore
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.util.ArrayList

data class Song(val name: String, val path: String)

class AndroidMediaQueryPlugin(activity: Activity): MethodCallHandler {
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "android_media_query")
      if(registrar == null || registrar.activity() == null)
        return;
      channel.setMethodCallHandler(AndroidMediaQueryPlugin(registrar.activity()))
    }
  }
  val _activity = activity;
  val _store = AndroidMediaStore(activity.applicationContext, _activity);

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if(call.method == "media.get") {
      val data = _store.musicList
      val singleSongMap = HashMap<String, String>();
      //val result = ArrayList<HashMap<String, String>>();
      for (song in data)
        singleSongMap[song.name] = song.path
      return result.success(singleSongMap);
    } else if(call.method == "media.check") {
      return result.success(_store.requestPermission() == PackageManager.PERMISSION_GRANTED);
    }
    else {
      result.notImplemented()
    }
  }
}

class AndroidMediaStore(private val context: Context, private val activity: Activity) {
  private val cr: ContentResolver
  var isPermissionGranted = true
  private val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME)

  val ringList: ArrayList<Song>
    get() = getRingList("")

  val musicList: ArrayList<Song>
    get() {
      val permission = requestPermission();
      if(permission == PackageManager.PERMISSION_GRANTED)
        return getMusicList("")
      return ArrayList<Song>()
    }

  init {
    this.cr = context.applicationContext.contentResolver
  }

  fun requestPermission() : Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (this.context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        activity.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                8787)
        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
        // app-defined int constant that should be quite unique
      }
      return this.context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    return PackageManager.PERMISSION_GRANTED;
  }

  fun getRingList(name: String?): ArrayList<Song> {
    //Some audio may be explicitly marked as not being music
    val selection = MediaStore.Audio.Media.IS_RINGTONE + " != 0"

    /*String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        520||仙劍奇俠傳4||仙劍奇俠傳4主題曲||/storage/0123-4567/Music/仙劍/仙劍奇俠傳4.mp3||仙劍奇俠傳4.mp3||240722*/

    val externalCursor = this.cr.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, null, null)

    val internalCursor = this.cr.query(
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
            projection,
            selection, null, null)

    val songs = ArrayList<Song>()
    songs.addAll(getSongInformations(externalCursor!!))
    songs.addAll(getSongInformations(internalCursor!!))
    //logSongLists(songs);
    externalCursor.close()
    internalCursor.close()
    if (name != null && name != "") {
      val filteredSongs = ArrayList<Song>()
      for (song in songs) {
        if (song.name.contains(name))
          filteredSongs.add(song)
      }
      return filteredSongs
    }
    return songs
  }

  fun getMusicList(name: String?): ArrayList<Song> {
    //Some audio may be explicitly marked as not being music
    val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    /*String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        520||仙劍奇俠傳4||仙劍奇俠傳4主題曲||/storage/0123-4567/Music/仙劍/仙劍奇俠傳4.mp3||仙劍奇俠傳4.mp3||240722*/

    val externalCursor = this.cr.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, null, null)

    val internalCursor = this.cr.query(
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
            projection,
            selection, null, null)

    val songs = ArrayList<Song>()
    songs.addAll(getSongInformations(externalCursor!!))
    songs.addAll(getSongInformations(internalCursor!!))
    //logSongLists(songs);
    externalCursor.close()
    internalCursor.close()
    if (name != null && name != "") {
      val filteredSongs = ArrayList<Song>()
      for (song in songs) {
        if (song.name.contains(name))
          filteredSongs.add(song)
      }
      return filteredSongs
    }
    return songs
  }

  private fun logSongLists(songs: ArrayList<Song>) {
    for (song in songs) {
      print(String.format(song.name + ":" + song.path))
    }
  }

  private fun getSongInformations(cursor: Cursor): ArrayList<Song> {
    val songs = ArrayList<Song>()
    while (cursor.moveToNext()) {
      val song = Song(cursor.getString(1), cursor.getString(0))
      //song.name = cursor.getString(1)
      //song.path = cursor.getString(0)
      songs.add(song)
    }
    return songs
  }

  fun checkSongExists(uri: String): Boolean {
    val file = File(uri)
    return file.exists()
  }
}

