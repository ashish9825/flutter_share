import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class FlutterShare {
  static const MethodChannel _channel = const MethodChannel('flutter_share');

  static const String _methodWhatsAppImages = 'whatsapp_share_images';
  static const String _methodWhatsAppText = 'whatsapp_share_text';

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> shareWhatsAppImages(
      String title, Map<String, List<int>> files, String mimeType,
      {String text = ''}) async {
    Map argsMap = <String, dynamic>{
      'title': '$title',
      'names': files.entries.toList().map((x) => x.key).toList(),
      'mimeType': mimeType,
      'text': '$text'
    };

    final tempDir = await getTemporaryDirectory();

    for (var entry in files.entries) {
      final file = await File('${tempDir.path}/${entry.key}').create();
      await file.writeAsBytes(entry.value);
    }

    await _channel.invokeMethod(_methodWhatsAppImages, argsMap);
  }

  static Future<void> shareWhatsAppText(
      String title, String text, String mimeType) async {
    Map argsMap = <String, dynamic>{
      'title': '$title',
      'mimeType': mimeType,
      'text': '$text'
    };

    await _channel.invokeMethod(_methodWhatsAppText, argsMap);
  }
}
