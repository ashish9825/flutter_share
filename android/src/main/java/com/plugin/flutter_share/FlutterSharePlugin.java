package com.plugin.flutter_share;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterSharePlugin */
public class FlutterSharePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native
  /// Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine
  /// and unregister it
  /// when the Flutter Engine is detached from the Activity
  final private static String _methodWhatsAppImages = "whatsapp_share_images";
  final private static String _methodWhatsAppText = "whatsapp_share_text";
 
  private Activity activity;
  private MethodChannel channel;

  public static void registerWith(Registrar registrar) {
    final FlutterSharePlugin instance = new FlutterSharePlugin();
    instance.onAttachedToEngine(registrar.messenger());
    instance.activity = registrar.activity();
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    onAttachedToEngine(flutterPluginBinding.getBinaryMessenger());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals(_methodWhatsAppImages)) {
      shareWhatsAppImages(call.arguments);
    } else if (call.method.equals(_methodWhatsAppText)) {
      shareWhatsAppText(call.arguments);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;
    activity = null;
  }

  private void onAttachedToEngine(BinaryMessenger messenger) {
    channel = new MethodChannel(messenger, "flutter_share");
    channel.setMethodCallHandler(this);
  }

  private void shareWhatsAppImages(Object arguments) {
    @SuppressWarnings("unchecked")
    HashMap<String, Object> argsMap = (HashMap<String, Object>) arguments;
    String title = (String) argsMap.get("title");

    @SuppressWarnings("unchecked")
    ArrayList<String> names = (ArrayList<String>) argsMap.get("names");
    String mimeType = (String) argsMap.get("mimeType");
    String text = (String) argsMap.get("text");
    Context activeContext = activity.getApplicationContext();

    Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    shareIntent.setType(mimeType);
    shareIntent.setPackage(false ? "com.whatsapp.w4b" : "com.whatsapp");

    ArrayList<Uri> contentUris = new ArrayList<>();

    for (String name : names) {
      File file = new File(activeContext.getCacheDir(), name);
      String fileProviderAuthority = activeContext.getPackageName() + ".provider";
      contentUris.add(FileProvider.getUriForFile(activeContext, fileProviderAuthority, file));
    }

    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris);
    if (!text.isEmpty())
      shareIntent.putExtra(Intent.EXTRA_TEXT, text);
    activity.startActivity(shareIntent);
  }

  private void shareWhatsAppText(Object arguments) {
    @SuppressWarnings("unchecked")
    HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
    String title = argsMap.get("title");
    String text = argsMap.get("text");
    String mimeType = argsMap.get("mimeType");

    Context activeContext = activity.getApplicationContext();

    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType(mimeType);
    shareIntent.setPackage(false ? "com.whatsapp.w4b" : "com.whatsapp");
    shareIntent.putExtra(Intent.EXTRA_TEXT, text);
    activity.startActivity(shareIntent);
  }

  @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
    }

}
